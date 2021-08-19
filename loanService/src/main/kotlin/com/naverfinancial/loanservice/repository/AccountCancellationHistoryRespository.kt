package com.naverfinancial.loanservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountCancellationHistoryRespository : JpaRepository<AccountCancellationHistoryRespository, Integer>{
}