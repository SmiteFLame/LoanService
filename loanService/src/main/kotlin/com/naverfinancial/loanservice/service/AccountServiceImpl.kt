package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.entity.account.dto.Account
import com.naverfinancial.loanservice.entity.account.dto.AccountCancellationHistory
import com.naverfinancial.loanservice.entity.account.dto.AccountTransactionHistory
import com.naverfinancial.loanservice.entity.account.repository.AccountCancellationHistoryRepository
import com.naverfinancial.loanservice.entity.account.repository.AccountRepository
import com.naverfinancial.loanservice.entity.account.repository.AccountTransactionHistoryRepository
import com.naverfinancial.loanservice.utils.AccountNumberGenerators
import com.naverfinancial.loanservice.utils.JsonFormData
import com.naverfinancial.loanservice.wrapper.CreditResult
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.sql.Timestamp

@Service
class AccountServiceImpl : AccountService {

    @Autowired
    lateinit var accountRepository : AccountRepository

    @Autowired
    lateinit var accountTransactionHistoryRepository: AccountTransactionHistoryRepository

    @Autowired
    lateinit var accountCancellationHistoryRepository: AccountCancellationHistoryRepository

    @Qualifier("account")
    @Autowired
    lateinit var accountTransactionManager: PlatformTransactionManager

    override fun selectAccountList(page : Int, size : Int)  : List<Account> {
        return accountRepository.findAll(PageRequest.of(page - 1, size)).toList();
    }

    override fun selectAccountByAccountId(accountId: Int): Account? {
        return accountRepository.findAccountbyAccountId(accountId)
    }

    override fun selectAccountListByNdi(ndi: String, page : Int, size : Int): List<Account> {
        return accountRepository.findAccountsByNdi(ndi, PageRequest.of(page - 1, size))
    }

    override fun selectAccountByNdiStatusNormal(ndi: String) : Account?{
        // 마이너스 통장 중복 검사
        val accounts = accountRepository.findAccountsByNdi(ndi)
        for(account in accounts){
            if(account.status == "normal"){
                return account
            }
        }
        return null
    }

    override fun openAccount(ndi: String, creditResult: CreditResult): Account {
        val status = accountTransactionManager.getTransaction(DefaultTransactionDefinition())

        // 통장번호 랜덤 생성
        var newAccountNumbers : String
        while(true) {
            newAccountNumbers = AccountNumberGenerators.generatorAccountNumbers()
            accountRepository.findAccountbyAccountNumbers(newAccountNumbers) ?: break
        }

        // 새로운 통장 개설
        var newAccount = Account(
            accountId = -1, // AUTO_INCREASED
            accountNumbers = newAccountNumbers,
            ndi = ndi,
            loanLimit = -5000,
            balance = 0,
            grade = creditResult.grade,
            status = "normal",
            createdDate = Timestamp(System.currentTimeMillis()),
        )

        newAccount = accountRepository.save(newAccount)

        accountTransactionManager.commit(status)

        return newAccount
    }

    override fun withdrawLoan(accountId: Int, amount: Int): Account {
        // 계좌 가져오기
        val account = selectAccountByAccountId(accountId)
        if(account == null || account.status != "normal"){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        // 대출 가능 조사하기
        if(account.balance + amount < account.loanLimit){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val status = accountTransactionManager.getTransaction(DefaultTransactionDefinition())

        val historyTime = Timestamp(System.currentTimeMillis())

        // 대출 기록 남기기
        val newAccountTransactionHistory = AccountTransactionHistory(
            historyId = -1, // AUTO_INCREASED
            amount = amount,
            type = "deposit",
            createdDate = historyTime,
            accountId = account.accountId,
            accountNumbers = account.accountNumbers
        )
        accountTransactionHistoryRepository.save(newAccountTransactionHistory)

        // 계좌 수정하기
        account.withdraw(amount, historyTime)
        val newAccount = accountRepository.save(account)

        accountTransactionManager.commit(status)

        return newAccount
    }

    override fun depositLoan(accountId : Int, amount: Int): Account {
       // 계좌 가져오기
        val account = selectAccountByAccountId(accountId)
        if(account == null || account.status != "normal"){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val status = accountTransactionManager.getTransaction(DefaultTransactionDefinition())

        // 반납 기록 남기기
        val newAccountTransactionHistory = AccountTransactionHistory(
            historyId = -1, // AUTO_INCREASED
            amount = amount,
            type = "deposit",
            createdDate = Timestamp(System.currentTimeMillis()),
            accountId = account.accountId,
            accountNumbers = account.accountNumbers
        )

        accountTransactionHistoryRepository.save(newAccountTransactionHistory)

        // 계좌 수정하기
        account.deposit(amount)
        val newAccount = accountRepository.save(account)

        accountTransactionManager.commit(status)

        return newAccount
    }

    override fun removeAccount(accountId: Int): Integer {
        // 계좌 가져오기=
        val account = selectAccountByAccountId(accountId)
        if(account == null || account.status != "normal"){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        if(account.balance < 0){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val status = accountTransactionManager.getTransaction(DefaultTransactionDefinition())

        val balance = account.balance

        // 계좌 상태 변경
        account.cancel()
        accountRepository.save(account)

        // 취소 기록 저장
        val accountCancellationHistory = AccountCancellationHistory(
            accountId = account.accountId,
            cancellationDate = Timestamp(System.currentTimeMillis())
        )

        accountCancellationHistoryRepository.save(accountCancellationHistory)

        accountTransactionManager.commit(status)

        return Integer(balance)
    }

    override fun searchGrade(ndi: String): CreditResult {
        val values = mapOf("ndi" to ndi)
        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/credits"))
            .POST(JsonFormData.formData(values))
            .header("Content-Type", "application/json")
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString());
        val grade = JSONObject(response.body()).getInt("grade")
        val isPermit = JSONObject(response.body()).getBoolean("isPermit")
        return CreditResult(grade, isPermit)
    }
}