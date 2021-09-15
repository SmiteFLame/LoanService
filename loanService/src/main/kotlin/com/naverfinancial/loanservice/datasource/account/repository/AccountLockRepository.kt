package com.naverfinancial.loanservice.datasource.account.repository

import com.naverfinancial.loanservice.datasource.account.dto.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.stereotype.Repository
import javax.persistence.LockModeType
import javax.persistence.QueryHint

@Repository
interface AccountLockRepository : JpaRepository<Account, Int> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(value = [QueryHint(name = "javax.persistence.lock.timeout", value = "10000")])
    fun findAccountByAccountId(accountId: Int): Account?
}
