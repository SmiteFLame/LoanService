package com.naverfinancial.loanservice.repository

import com.naverfinancial.loanservice.dto.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AccountRespository : JpaRepository<Account, Integer> {
//    fun findAccountbyAccountNumbers(accountNumbers : String) : Optional<Account>
    fun findAccountsByNDI(NDI : String) : List<Account>
}