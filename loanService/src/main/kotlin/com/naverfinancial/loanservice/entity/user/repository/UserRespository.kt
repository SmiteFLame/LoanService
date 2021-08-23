package com.naverfinancial.loanservice.entity.user.repository

import com.naverfinancial.loanservice.entity.user.dto.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRespository : JpaRepository<User, Integer> {
    fun findUserByEmail(email : String) : Optional<User>
    fun findUserByNDI(NDI : String) : Optional<User>
}