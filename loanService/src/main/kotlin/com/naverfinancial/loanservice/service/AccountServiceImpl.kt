package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.dto.Account
import com.naverfinancial.loanservice.dto.User
import com.naverfinancial.loanservice.repository.AccountRespository
import com.naverfinancial.loanservice.repository.AccountTransactionHistoryRespository
import com.naverfinancial.loanservice.repository.UserRespository
import com.naverfinancial.loanservice.utils.AccountNumberGenerators
import com.naverfinancial.loanservice.utils.JsonFormData
import com.naverfinancial.loanservice.wrapper.CreditResult
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.sql.Timestamp
import java.util.*
import javax.swing.text.html.Option

@Service
class AccountServiceImpl : AccountService {

    @Autowired
    lateinit var accountRespository : AccountRespository

    @Autowired
    lateinit var userRespository: UserRespository

    @Autowired
    lateinit var accountTransactionHistoryRespository: AccountTransactionHistoryRespository

    override fun searchAll()  : List<Account> {
        return accountRespository.findAll();
    }

    override fun searchByAccountNumbers(account_numbers: String): Optional<Account> {
        TODO("Not yet implemented")
        //return accountRespository.findAccountbyAccount_number(account_numbers)
    }

    override fun searchByNDI(NDI: String): List<Account> {
        return accountRespository.findAccountsByNDI(NDI)
    }

    override fun openAccount(NDI: String, creditResult: CreditResult): Account {
        var accounts = searchByNDI(NDI)
        for(account in accounts){
            if(account.getStatus() == "normal"){
                // 마이너스 통장이 이미 존재하면 존재하는 통장 Return
                return account
            }
        }

        while(true){
            var newAccountNumbers : String = AccountNumberGenerators.generatorAccountNumbers()
            var check = searchByAccountNumbers(newAccountNumbers)
            if(check.isEmpty()){
                break
            }
        }

        var newAccount = Account(
            accountId = -1, // AUTO_INCREASED
            accountNumbers = newAccountNumbers,
            NDI = NDI,
            loanLimit = 5000,
            balance = 0,
            grade = creditResult.getGrade(),
            status = "normal",
            createdDate = Timestamp(System.currentTimeMillis()),
        )

        return accountRespository.save(newAccount)
    }

    override fun depositLoan(account_numbers: String, amount: Int): Optional<Account> {
        TODO("Not yet implemented")
    }

    override fun withdrawLoan(account_numbers: String, amount: Int): Optional<Account> {
        TODO("Not yet implemented")
    }

    override fun cancelAccount(account_numbers: String) {
        TODO("Not yet implemented")
    }

    override fun searchGrade(NDI: String): CreditResult {
        val values = mapOf("NDI" to NDI)
        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/credits"))
            .POST(JsonFormData.formData(values))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString());
        val grade = JSONObject(response.body()).getInt("grade")
        val isPermit = JSONObject(response.body()).getBoolean("isPermit")
        return CreditResult(grade, isPermit)
    }
}