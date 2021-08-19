package com.naverfinancial.loanservice.repository

import com.naverfinancial.loanservice.dto.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRespository : JpaRepository<User, Integer> {
    fun findUserByEmail(email : String) : Optional<User>
    fun findUserByNDI(NDI : String) : Optional<User>
}