package com.naverfinancial.loanservice.datasource.user.repository

import com.naverfinancial.loanservice.datasource.user.dto.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun findUserByEmail(email: String): User?
    fun findUserByNdi(ndi: String): User?
}
