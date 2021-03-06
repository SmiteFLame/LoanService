package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.datasource.account.dto.Account
import com.naverfinancial.loanservice.datasource.account.dto.AccountCancellationHistory
import com.naverfinancial.loanservice.datasource.account.dto.AccountTransactionHistory
import com.naverfinancial.loanservice.datasource.account.repository.AccountCancellationHistoryRepository
import com.naverfinancial.loanservice.datasource.account.repository.AccountLockRepository
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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Service
class AccountServiceImpl : AccountService {

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var accountLockRepository: AccountLockRepository

    @Autowired
    lateinit var accountTransactionHistoryRepository: AccountTransactionHistoryRepository

    @Autowired
    lateinit var accountCancellationHistoryRepository: AccountCancellationHistoryRepository

    override fun selectAccountByAccountID(accountId: Int): Account {
        return accountRepository.findAccountByAccountId(accountId)
            ?: throw AccountException.NullAccountException()
    }

    override fun selectAccounts(
        ndi: String?,
        status: AccountTypeStatus,
        offsetBasedPageRequest: OffsetBasedPageRequest
    ): Page<Account> {
        return if (ndi != null && status == AccountTypeStatus.ALL) {
            accountRepository.findAccountsByNdi(ndi, offsetBasedPageRequest)
        } else if (ndi != null) {
            accountRepository.findAccountsByNdiAndStatus(ndi, status, offsetBasedPageRequest)
        } else if (status == AccountTypeStatus.ALL) {
            accountRepository.findAll(offsetBasedPageRequest)
        } else {
            accountRepository.findAccountsByStatus(status, offsetBasedPageRequest)
        }
    }

    override fun openAccount(ndi: String, userCreditRating: UserCreditRating): Account {
        // ???????????? ?????? ??????
        var newAccountNumbers: String
        while (true) {
            newAccountNumbers = AccountNumberGenerators.generatorAccountNumbers()
            accountRepository.findAccountByAccountNumber(newAccountNumbers) ?: break
        }

        // ????????? ?????? ??????
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
    override fun withdrawLoan(accountId: Int, amount: Int): Account {
        val account =
            accountLockRepository.findAccountByAccountId(accountId) ?: throw AccountException.NullAccountException()

        // ?????? ?????? ????????????
        if (account.balance - amount < account.loanLimit) {
            throw AccountException.OverLimitException()
        }

        account.withdraw(amount)

        // ?????? ?????? ?????????
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

        // ?????? ????????????
        return updateAccount(account)
    }

    @Transactional(value = "accountTransactionManager")
    override fun depositLoan(accountId: Int, amount: Int): Account {
        val account =
            accountLockRepository.findAccountByAccountId(accountId) ?: throw AccountException.NullAccountException()

        account.deposit(amount)

        // ?????? ???????????? ????????? ?????? ????????? ?????? ??????
        if (account.balance > amount) {
            return updateAccount(account)
        }

        var translatedAmount = amount
        // ?????? ????????? ????????? ??????
        if (account.balance > 0) {
            translatedAmount -= account.balance
        }

        // ?????? ?????? ?????????
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
            accountLockRepository.findAccountByAccountId(accountId) ?: throw AccountException.NullAccountException()
        if (account.balance != 0) {
            throw AccountException.RestLimitException()
        }

        // ?????? ?????? ??????
        account.cancel(Timestamp(System.currentTimeMillis()))
        updateAccount(account)

        // ?????? ?????? ??????
        accountCancellationHistoryRepository.save(
            AccountCancellationHistory(
                accountId = account.accountId,
                cancellationDate = Timestamp(System.currentTimeMillis())
            )
        )
    }

    fun updateAccount(account: Account): Account {
        return accountLockRepository.save(account)
    }
}
