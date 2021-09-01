package com.naverfinancial.creditrating.datasource.user.repository

import com.naverfinancial.creditrating.datasource.user.dto.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun findUserByNdi(ndi: String): User?
}
