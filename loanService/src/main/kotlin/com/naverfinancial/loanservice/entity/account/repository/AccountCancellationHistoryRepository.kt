package com.naverfinancial.loanservice.entity.account.repository

import com.naverfinancial.loanservice.entity.account.dto.AccountCancellationHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountCancellationHistoryRepository : JpaRepository<AccountCancellationHistory, Integer>{
}