package com.naverfinancial.loanservice.entity.account.repository

import com.naverfinancial.loanservice.entity.account.dto.AccountTransactionHistory
import org.springframework.data.jpa.repository.JpaRepository

interface AccountTransactionHistoryRespository : JpaRepository<AccountTransactionHistory, Integer> {
}