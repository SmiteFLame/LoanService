package com.naverfinancial.loanservice.datasource.account.repository

import com.naverfinancial.loanservice.datasource.account.dto.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.stereotype.Repository
import javax.persistence.LockModeType

@Repository
interface AccountLockRepository : JpaRepository<Account, Int> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findAccountByAccountId(accountId: Int): Account?
}
