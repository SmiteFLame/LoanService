package com.naverfinancial.creditrating.entity.user.repository

import com.naverfinancial.creditrating.entity.user.dto.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRespository : JpaRepository<User, Integer> {
    fun findUserByNDI(NDI : String) : User?
}