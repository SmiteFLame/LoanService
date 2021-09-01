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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Timestamp

@Service
class AccountServiceImpl : AccountService {

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var accountTransactionHistoryRepository: AccountTransactionHistoryRepository

    @Autowired
    lateinit var accountCancellationHistoryRepository: AccountCancellationHistoryRepository

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

    override fun withdrawLoan(account: Account, amount: Int): Account {
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
        return accountRepository.save(account)
    }

    override fun depositLoan(account: Account, amount: Int): Account {
        account.deposit(amount)
        val newAccount = accountRepository.save(account)

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

        return newAccount
    }

    override fun removeAccount(account: Account): Int {
        if (account.balance < 0) {
            throw AccountException.RestLimitException()
        }

        val balance = account.balance

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

        return balance
    }
}
