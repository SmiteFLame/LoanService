package com.naverfinancial.loanservice.repository

import com.naverfinancial.loanservice.dto.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRespository : JpaRepository<User, Integer> {
}