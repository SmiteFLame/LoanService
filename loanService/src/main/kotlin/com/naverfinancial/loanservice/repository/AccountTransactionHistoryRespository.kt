package com.naverfinancial.loanservice.repository

import com.naverfinancial.loanservice.dto.AccountTransactionHistory
import org.springframework.data.jpa.repository.JpaRepository

interface AccountTransactionHistoryRespository : JpaRepository<AccountTransactionHistory, Integer> {
}