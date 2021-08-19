package com.naverfinancial.loanservice.repository

import com.naverfinancial.loanservice.dto.AccountCancellationHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountCancellationHistoryRespository : JpaRepository<AccountCancellationHistory, Integer>{
}