package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.dto.Account
import com.naverfinancial.loanservice.dto.AccountTransactionHistory
import com.naverfinancial.loanservice.repository.AccountRespository
import com.naverfinancial.loanservice.repository.AccountTransactionHistoryRespository
import com.naverfinancial.loanservice.utils.AccountNumberGenerators
import com.naverfinancial.loanservice.utils.JsonFormData
import com.naverfinancial.loanservice.wrapper.CreditResult
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
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

    override fun searchAll()  : List<Account> {
        return accountRespository.findAll();
    }

    override fun searchByAccountNumbers(accountNumbers: String): Optional<Account> {
        var account = searchByAccountNumbers(accountNumbers)
        if(account.isEmpty){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }
        return account
    }

    override fun searchByNDI(NDI: String): List<Account> {
        return accountRespository.findAccountsByNDI(NDI)
    }

    override fun openAccount(NDI: String, creditResult: CreditResult): Account {
        // 마이너스 통장 중복 검사
        var accounts = searchByNDI(NDI)
        for(account in accounts){
            if(account.getStatus() == "normal"){
                return account
            }
        }

        // 통장번호 랜덤 생성
        var newAccountNumbers : String
        while(true) {
            newAccountNumbers = AccountNumberGenerators.generatorAccountNumbers()
            var check = searchByAccountNumbers(newAccountNumbers)
            if (check.isEmpty) {
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
            grade = creditResult.getGrade(),
            status = "normal",
            createdDate = Timestamp(System.currentTimeMillis()),
        )

        return accountRespository.save(newAccount)
    }

    override fun withdrawLoan(accountNumbers: String, amount: Int): Account {
        // 계좌 가져오기
        var account = searchByAccountNumbers(accountNumbers)

        // 대출 가능 조사하기
        // 잔고 + 대출을 한 후에 현재 한도 금액보다 작아진다면
        if(account.get().getBalance() + amount < account.get().getLoanLimit()){
            throw ResponseStatusException(HttpStatus.NOT_ACCEPTABLE)
        }

        var historyTime = Timestamp(System.currentTimeMillis())
        // 대출 기록 남기기
        var newAccountTransactionHistory = AccountTransactionHistory(
            historyId = -1, // AUTO_INCREASED
            amount = amount,
            type = "deposit",
            createdDate = historyTime,
            accountId = account.get().getAccountID(),
            accountNumbers = account.get().getAccountNumbers()
        )

        accountTransactionHistoryRespository.save(newAccountTransactionHistory)

        // 계좌 수정하기
        account.get().withdraw(amount, historyTime)

        return accountRespository.save(account.get())
    }

    override fun depositLoan(accountNumbers: String, amount: Int): Account {
        // 계좌 가져오기
        var account = searchByAccountNumbers(accountNumbers)

        // 반납 기록 남기기
        var newAccountTransactionHistory = AccountTransactionHistory(
            historyId = -1, // AUTO_INCREASED
            amount = amount,
            type = "deposit",
            createdDate = Timestamp(System.currentTimeMillis()),
            accountId = account.get().getAccountID(),
            accountNumbers = account.get().getAccountNumbers()
        )

        accountTransactionHistoryRespository.save(newAccountTransactionHistory)

        // 계좌 수정하기
        account.get().deposit(amount)

        return accountRespository.save(account.get())
    }

    override fun cancelAccount(accountNumbers: String): Boolean {
        // 계좌 가져오기
        var account = searchByAccountNumbers(accountNumbers)

        if(account.get().getBalance() < 0){
            throw ResponseStatusException(HttpStatus.NOT_ACCEPTABLE)
        }

        TODO("Not yet implemented")
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