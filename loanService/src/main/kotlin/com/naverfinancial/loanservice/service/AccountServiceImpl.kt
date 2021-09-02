package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.datasource.account.dto.Account
import com.naverfinancial.loanservice.datasource.account.dto.AccountCancellationHistory
import com.naverfinancial.loanservice.datasource.account.dto.AccountTransactionHistory
import com.naverfinancial.loanservice.datasource.account.repository.AccountCancellationHistoryRepository
import com.naverfinancial.loanservice.datasource.account.repository.AccountRepository
import com.naverfinancial.loanservice.datasource.account.repository.AccountTransactionHistoryRepository
import com.naverfinancial.loanservice.datasource.user.dto.UserCreditRating
import com.naverfinancial.loanservice.enumclass.AccountRequestTypeStatus
import com.naverfinancial.loanservice.enumclass.AccountTypeStatus
import com.naverfinancial.loanservice.exception.AccountException
import com.naverfinancial.loanservice.utils.AccountNumberGenerators
import com.naverfinancial.loanservice.utils.OffsetBasedPageRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.Lock
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import javax.persistence.LockModeType

@Service
class AccountServiceImpl : AccountService {

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var accountTransactionHistoryRepository: AccountTransactionHistoryRepository

    @Autowired
    lateinit var accountCancellationHistoryRepository: AccountCancellationHistoryRepository

    @Transactional(value = "accountTransactionManager")
    override fun selectAccounts(ndi: String?, status: AccountTypeStatus, limit: Int, offset: Long): Page<Account> {
        return if (ndi != null && status == AccountTypeStatus.ALL) {
            accountRepository.findAccountsByNdi(ndi, OffsetBasedPageRequest(limit, offset))
        } else if (ndi != null) {
            accountRepository.findAccountsByNdiAndStatus(
                ndi,
                status,
                OffsetBasedPageRequest(limit, offset)
            )
        } else if (status == AccountTypeStatus.ALL) {
            accountRepository.findAll(OffsetBasedPageRequest(limit, offset))
        } else {
            accountRepository.findAccountsByStatus(status, OffsetBasedPageRequest(limit, offset))
        }
    }

    @Transactional(value = "accountTransactionManager")
    override fun openAccount(ndi: String, userCreditRating: UserCreditRating): Account {

        // 통장번호 랜덤 생성
        var newAccountNumbers: String
        while (true) {
            newAccountNumbers = AccountNumberGenerators.generatorAccountNumbers()
            accountRepository.findAccountByAccountNumber(newAccountNumbers) ?: break
        }

        // 새로운 통장 개설
        return accountRepository.save(
            Account(
                accountId = -1, // AUTO_INCREASED
                accountNumber = newAccountNumbers,
                ndi = ndi,
                loanLimit = -5000,
                balance = 0,
                grade = userCreditRating.grade,
                status = AccountTypeStatus.NORMAL,
                createdDate = Timestamp(System.currentTimeMillis()),
            )
        )
    }

    @Transactional(value = "accountTransactionManager")
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    override fun withdrawLoan(accountId: Int, amount: Int): Account {
        val account =
            accountRepository.findAccountByAccountId(accountId) ?: throw AccountException.NullAccountException()

        // 대출 가능 조사하기
        if (account.balance - amount < account.loanLimit) {
            throw AccountException.OverLimitException()
        }

        account.withdraw(amount)

        // 대출 기록 남기기
        accountTransactionHistoryRepository.save(
            AccountTransactionHistory(
                historyId = -1, // AUTO_INCREASED
                amount = amount,
                type = AccountRequestTypeStatus.WITHDRAW,
                translatedDate = Timestamp(System.currentTimeMillis()),
                accountId = account.accountId,
                accountNumber = account.accountNumber
            )
        )

        // 계좌 수정하기
        return updateAccount(account)
    }

    @Transactional(value = "accountTransactionManager")
    @Lock(value = LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    override fun depositLoan(accountId: Int, amount: Int): Account {
        val account =
            accountRepository.findAccountByAccountId(accountId) ?: throw AccountException.NullAccountException()

        account.deposit(amount)

        // 이미 마이너스 통장이 아닌 상태로 넣은 경우
        if (account.balance > amount) {
            return updateAccount(account)
        }

        var translatedAmount = amount
        // 거래 금액이 초과된 경우
        if (account.balance > 0) {
            translatedAmount -= account.balance
        }

        // 반납 기록 남기기
        accountTransactionHistoryRepository.save(
            AccountTransactionHistory(
                historyId = -1, // AUTO_INCREASED
                amount = translatedAmount,
                type = AccountRequestTypeStatus.DEPOSIT,
                translatedDate = Timestamp(System.currentTimeMillis()),
                accountId = account.accountId,
                accountNumber = account.accountNumber
            )
        )

        return updateAccount(account)
    }

    @Transactional(value = "accountTransactionManager")
    override fun removeAccount(accountId: Int) {
        val account =
            accountRepository.findAccountByAccountId(accountId) ?: throw AccountException.NullAccountException()
        if (account.balance != 0) {
            throw AccountException.RestLimitException()
        }

        // 계좌 상태 변경
        account.cancel(Timestamp(System.currentTimeMillis()))
        accountRepository.save(account)

        // 취소 기록 저장
        accountCancellationHistoryRepository.save(
            AccountCancellationHistory(
                accountId = account.accountId,
                cancellationDate = Timestamp(System.currentTimeMillis())
            )
        )
    }

    fun updateAccount(account: Account): Account {
        return accountRepository.save(account)
    }
}
