package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.dto.Account
import com.naverfinancial.loanservice.dto.User
import com.naverfinancial.loanservice.repository.AccountRespository
import com.naverfinancial.loanservice.repository.AccountTransactionHistoryRespository
import com.naverfinancial.loanservice.repository.UserRespository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

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

    override fun searchByAccountNumbers(account_numbers: String): Account {
        TODO("Not yet implemented")
    }

    override fun searchByNDI(NDI: String): Account {
        TODO("Not yet implemented")
    }

    override fun openAccount(user: User): Account {
        TODO("Not yet implemented")
    }

    override fun depositLoan(account_numbers: String, amount: Int): Account {
        TODO("Not yet implemented")
    }

    override fun withdrawLoan(account_numbers: String, amount: Int): Account {
        TODO("Not yet implemented")
    }

    override fun cancelAccount(account_numbers: String) {
        TODO("Not yet implemented")
    }
}