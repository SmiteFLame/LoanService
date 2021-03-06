package com.naverfinancial.loanservice.datasource.account.repository

import com.naverfinancial.loanservice.datasource.account.dto.Account
import com.naverfinancial.loanservice.enumclass.AccountTypeStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : JpaRepository<Account, Int> {
    fun findAccountByAccountNumber(accountNumber: String): Account?
    fun findAccountByAccountId(accountId: Int): Account?
    fun findAccountByNdiAndStatus(ndi: String, status: AccountTypeStatus): Account?
    fun findAccountsByNdiAndStatus(ndi: String, status: AccountTypeStatus, pageable: Pageable): Page<Account>
    fun findAccountsByNdi(ndi: String, pageable: Pageable): Page<Account>
    fun findAccountsByStatus(status: AccountTypeStatus, pageable: Pageable): Page<Account>
}
