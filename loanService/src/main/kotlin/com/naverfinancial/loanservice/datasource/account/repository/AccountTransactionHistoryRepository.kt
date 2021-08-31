package com.naverfinancial.loanservice.datasource.account.repository

import com.naverfinancial.loanservice.datasource.account.dto.AccountTransactionHistory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface AccountTransactionHistoryRepository : JpaRepository<AccountTransactionHistory, Integer> {
    fun findAccountTransactionHistoriesByAccountId(accountId: Int, pageable: Pageable): Page<AccountTransactionHistory>
}
