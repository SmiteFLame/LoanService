package com.naverfinancial.loanservice.repository

import com.naverfinancial.loanservice.dto.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRespository : JpaRepository<Account, Integer> {

}