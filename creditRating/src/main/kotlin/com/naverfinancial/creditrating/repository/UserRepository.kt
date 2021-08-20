package com.naverfinancial.creditrating.repository

import com.naverfinancial.creditrating.dto.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRespository : JpaRepository<User, Integer> {
    fun findUserByNDI(NDI : String) : User
}