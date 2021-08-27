package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.entity.account.dto.Account
import com.naverfinancial.loanservice.entity.account.dto.AccountCancellationHistory
import com.naverfinancial.loanservice.entity.account.dto.AccountTransactionHistory
import com.naverfinancial.loanservice.entity.account.repository.AccountCancellationHistoryRepository
import com.naverfinancial.loanservice.entity.account.repository.AccountRepository
import com.naverfinancial.loanservice.entity.account.repository.AccountTransactionHistoryRepository
import com.naverfinancial.loanservice.entity.user.dto.UserCreditRating
import com.naverfinancial.loanservice.enumclass.AccountRequestTypeStatus
import com.naverfinancial.loanservice.enumclass.AccountTypeStatus
import com.naverfinancial.loanservice.exception.OverLimitException
import com.naverfinancial.loanservice.exception.RestLimitException
import com.naverfinancial.loanservice.utils.AccountNumberGenerators
import com.naverfinancial.loanservice.utils.PagingUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Service
@Transactional("accountTransactionManager")
class AccountServiceImpl : AccountService {

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var accountTransactionHistoryRepository: AccountTransactionHistoryRepository

    @Autowired
    lateinit var accountCancellationHistoryRepository: AccountCancellationHistoryRepository

    override fun selectAccountList(limit: Int, offset: Int): List<Account> {
        return accountRepository.findAll(PageRequest.of(PagingUtil.getPage(limit, offset), limit)).toList();
    }

    override fun selectAccountByAccountId(accountId: Int): Account? {
        return accountRepository.findAccountbyAccountId(accountId)
    }

    override fun selectAccountListByNdi(ndi: String, limit: Int, offset: Int): List<Account> {
        return accountRepository.findAccountsByNdi(ndi, PageRequest.of(PagingUtil.getPage(limit, offset), limit))
    }

    override fun selectAccountByNdiStatusNormal(ndi: String): Account? {
        // 마이너스 통장 중복 검사
        val accounts = accountRepository.findAccountsByNdi(ndi)
        for (account in accounts) {
            if (account.status == AccountTypeStatus.NORMAL) {
                return account
            }
        }
        return null
    }

    override fun openAccount(ndi: String, userCreditRating: UserCreditRating): Account {

        // 통장번호 랜덤 생성
        var newAccountNumbers: String
        while (true) {
            newAccountNumbers = AccountNumberGenerators.generatorAccountNumbers()
            accountRepository.findAccountbyAccountNumber(newAccountNumbers) ?: break
        }

        // 새로운 통장 개설
        var newAccount = Account(
            accountId = -1, // AUTO_INCREASED
            accountNumber = newAccountNumbers,
            ndi = ndi,
            loanLimit = -5000,
            balance = 0,
            grade = userCreditRating.grade,
            status = AccountTypeStatus.NORMAL,
            createdDate = Timestamp(System.currentTimeMillis()),
        )

        newAccount = accountRepository.saveAndFlush(newAccount)

        return newAccount
    }

    override fun withdrawLoan(account: Account, amount: Int): Account {
        // 대출 가능 조사하기
        if (account.balance - amount < account.loanLimit) {
            throw OverLimitException()
        }

        account.withdraw(amount)


        // 대출 기록 남기기
        val newAccountTransactionHistory = AccountTransactionHistory(
            historyId = -1, // AUTO_INCREASED
            amount = amount,
            type = AccountRequestTypeStatus.WITHDRAW,
            translatedDate = Timestamp(System.currentTimeMillis()),
            accountId = account.accountId,
            accountNumber = account.accountNumber
        )
        accountTransactionHistoryRepository.save(newAccountTransactionHistory)


        // 계좌 수정하기
        return accountRepository.save(account)
    }

    override fun depositLoan(account: Account, amount: Int): Account {
        account.deposit(amount)
        var newAccount = accountRepository.save(account)

        // 이미 마이너스 통장이 아닌 상태로 넣은 경우
        if (newAccount.balance > amount) {
            return newAccount
        }

        var translatedAmount = amount
        // 거래 금액이 초과된 경우
        if (newAccount.balance > 0) {
            translatedAmount -= newAccount.balance
        }

        // 반납 기록 남기기
        val newAccountTransactionHistory = AccountTransactionHistory(
            historyId = -1, // AUTO_INCREASED
            amount = translatedAmount,
            type = AccountRequestTypeStatus.DEPOSIT,
            translatedDate = Timestamp(System.currentTimeMillis()),
            accountId = account.accountId,
            accountNumber = account.accountNumber
        )

        accountTransactionHistoryRepository.save(newAccountTransactionHistory)

        return newAccount
    }

    override fun removeAccount(account: Account): Integer {
        if (account.balance < 0) {
            throw RestLimitException()
        }

        val balance = account.balance

        // 계좌 상태 변경
        account.cancel(Timestamp(System.currentTimeMillis()))
        accountRepository.save(account)

        // 취소 기록 저장
        val accountCancellationHistory = AccountCancellationHistory(
            accountId = account.accountId,
            cancellationDate = Timestamp(System.currentTimeMillis())
        )

        accountCancellationHistoryRepository.save(accountCancellationHistory)

        return Integer(balance)
    }

    override fun selectAccountTransactionList(limit: Int, offset: Int): List<AccountTransactionHistory> {
        return accountTransactionHistoryRepository.findAll(PageRequest.of(PagingUtil.getPage(limit, offset), limit)).toList()
    }

    override fun selectAccountTransactionListByAccountId(
        account: Account,
        limit: Int, offset: Int
    ): List<AccountTransactionHistory> {
        return accountTransactionHistoryRepository.findAccountTransactionHistoriesByAccountId(
            account.accountId,
            PageRequest.of(PagingUtil.getPage(limit, offset), limit)
        ).toList()
    }
}
