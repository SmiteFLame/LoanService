package com.naverfinancial.loanservice.entity.account.repository

import com.naverfinancial.loanservice.entity.account.dto.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AccountRespository : JpaRepository<Account, Integer> {
    @Query("select acc from Account acc where acc.accountNumbers = ?1")
    fun findAccountbyAccountNumbers(accountNumbers : String) : Account?
    fun findAccountsByNDI(NDI : String) : List<Account>
}