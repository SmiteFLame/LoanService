package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.entity.account.dto.Account
import com.naverfinancial.loanservice.entity.account.dto.AccountCancellationHistory
import com.naverfinancial.loanservice.entity.account.dto.AccountTransactionHistory
import com.naverfinancial.loanservice.entity.account.repository.AccountCancellationHistoryRespository
import com.naverfinancial.loanservice.entity.account.repository.AccountRespository
import com.naverfinancial.loanservice.entity.account.repository.AccountTransactionHistoryRespository
import com.naverfinancial.loanservice.utils.AccountNumberGenerators
import com.naverfinancial.loanservice.utils.JsonFormData
import com.naverfinancial.loanservice.wrapper.CreditResult
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
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
import java.util.*

@Service
class AccountServiceImpl : AccountService {

    @Autowired
    lateinit var accountRespository : AccountRespository

    @Autowired
    lateinit var accountTransactionHistoryRespository: AccountTransactionHistoryRespository

    @Autowired
    lateinit var accountCancellationHistoryRespository: AccountCancellationHistoryRespository

    @Qualifier("user")
    @Autowired
    lateinit var userTransactionManager: PlatformTransactionManager

    @Qualifier("account")
    @Autowired
    lateinit var accountTransactionManager: PlatformTransactionManager

    override fun searchAll()  : List<Account> {
        return accountRespository.findAll();
    }

    override fun searchByAccountNumbers(accountNumbers: String): Account? {
        return accountRespository.findAccountbyAccountNumbers(accountNumbers)
    }

    override fun searchByNDI(NDI: String): List<Account> {
        return accountRespository.findAccountsByNDI(NDI)
    }

    override fun openAccount(NDI: String, creditResult: CreditResult): Account {
        var status = accountTransactionManager.getTransaction(DefaultTransactionDefinition())

        // 마이너스 통장 중복 검사
        var accounts = searchByNDI(NDI)
        for(account in accounts){
            if(account.status == "normal"){
                return account
            }
        }
        // 통장번호 랜덤 생성
        var newAccountNumbers : String
        while(true) {
            newAccountNumbers = AccountNumberGenerators.generatorAccountNumbers()
            var check = searchByAccountNumbers(newAccountNumbers)
            if (check == null) {
                break
            }
        }

        // 새로운 통장 개설
        var newAccount = Account(
            accountId = -1, // AUTO_INCREASED
            accountNumbers = newAccountNumbers,
            NDI = NDI,
            loanLimit = -5000,
            balance = 0,
            grade = creditResult.grade,
            status = "normal",
            createdDate = Timestamp(System.currentTimeMillis()),
        )

        newAccount = accountRespository.save(newAccount)

        accountTransactionManager.commit(status)

        return newAccount
    }

    override fun withdrawLoan(accountNumbers: String, amount: Int): Account {
        var status = accountTransactionManager.getTransaction(DefaultTransactionDefinition())

        // 계좌 가져오기
        var account = searchByAccountNumbers(accountNumbers)
        if(account == null || !account.status.equals("normal")){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        // 대출 가능 조사하기
        // 잔고 + 대출을 한 후에 현재 한도 금액보다 작아진다면
        if(account.balance + amount < account.loanLimit){
            throw ResponseStatusException(HttpStatus.NOT_ACCEPTABLE)
        }

        var historyTime = Timestamp(System.currentTimeMillis())
        // 대출 기록 남기기
        var newAccountTransactionHistory = AccountTransactionHistory(
            historyId = -1, // AUTO_INCREASED
            amount = amount,
            type = "deposit",
            createdDate = historyTime,
            accountId = account.accountId,
            accountNumbers = account.accountNumbers
        )
        accountTransactionHistoryRespository.save(newAccountTransactionHistory)

        // 계좌 수정하기
        account.withdraw(amount, historyTime)
        var newAccount = accountRespository.save(account)

        accountTransactionManager.commit(status)

        return newAccount
    }

    override fun depositLoan(accountNumbers: String, amount: Int): Account {
        var status = accountTransactionManager.getTransaction(DefaultTransactionDefinition())

        // 계좌 가져오기
        var account = searchByAccountNumbers(accountNumbers)
        if(account == null || !account.status.equals("normal")){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        // 반납 기록 남기기
        var newAccountTransactionHistory = AccountTransactionHistory(
            historyId = -1, // AUTO_INCREASED
            amount = amount,
            type = "deposit",
            createdDate = Timestamp(System.currentTimeMillis()),
            accountId = account.accountId,
            accountNumbers = account.accountNumbers
        )

        accountTransactionHistoryRespository.save(newAccountTransactionHistory)

        // 계좌 수정하기
        account.deposit(amount)
        var newAccount = accountRespository.save(account)

        accountTransactionManager.commit(status)

        return newAccount
    }

    override fun cancelAccount(accountNumbers: String): Integer {
        var status = accountTransactionManager.getTransaction(DefaultTransactionDefinition())

        // 계좌 가져오기
        var account = searchByAccountNumbers(accountNumbers)
        if(account == null || !account.status.equals("normal")){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        if(account.balance < 0){
            throw ResponseStatusException(HttpStatus.NOT_ACCEPTABLE)
        }

        var balance = account.balance
        // 계좌 상태 변경
        account.cancel()
        accountRespository.save(account)

        // 취소 기록 저장
        var accountCancellationHistory = AccountCancellationHistory(
            accountId = account.accountId,
            cancellationDate = Timestamp(System.currentTimeMillis())
        )

        accountCancellationHistoryRespository.save(accountCancellationHistory)

        accountTransactionManager.commit(status)

        return Integer(balance)
    }

    override fun searchGrade(NDI: String): CreditResult {
        val values = mapOf("NDI" to NDI)
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