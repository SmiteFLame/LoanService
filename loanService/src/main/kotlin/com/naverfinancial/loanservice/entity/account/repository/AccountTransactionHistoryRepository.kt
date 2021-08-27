package com.naverfinancial.loanservice.entity.account.repository

import com.naverfinancial.loanservice.entity.account.dto.AccountTransactionHistory
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface AccountTransactionHistoryRepository : JpaRepository<AccountTransactionHistory, Integer>{
    fun findAccountTransactionHistoriesByAccountId(accountId: Int, pageable: Pageable) : List<AccountTransactionHistory>
}
