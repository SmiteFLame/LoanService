package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.dto.User
import com.naverfinancial.loanservice.wrapper.Register
import java.util.*

interface UserService {
    fun searchUserByEmails(emails : String) : Optional<User>
    fun searchUserByNDI(NDI : String) : User
    fun saveUser(register : Register) : User
}