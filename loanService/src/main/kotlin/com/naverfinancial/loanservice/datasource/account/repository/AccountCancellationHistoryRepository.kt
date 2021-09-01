package com.naverfinancial.loanservice.datasource.account.repository

import com.naverfinancial.loanservice.datasource.account.dto.AccountCancellationHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountCancellationHistoryRepository : JpaRepository<AccountCancellationHistory, Int>
