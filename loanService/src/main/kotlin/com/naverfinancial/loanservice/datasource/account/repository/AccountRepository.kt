package com.naverfinancial.loanservice.datasource.account.repository

import com.naverfinancial.loanservice.datasource.account.dto.Account
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : JpaRepository<Account, Integer> {
    @Query("select acc from Account acc where acc.accountNumber = ?1")
    fun findAccountbyAccountNumber(accountNumber: String): Account?

    @Query("select acc from Account acc where acc.accountId = ?1")
    fun findAccountbyAccountId(accountId: Int): Account?
    fun findAccountsByNdi(ndi: String): List<Account>
    fun findAccountsByNdi(ndi: String, pageable: Pageable): List<Account>
}
