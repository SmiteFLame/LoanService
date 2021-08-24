package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.entity.user.dto.User
import java.util.*

interface UserService {
    fun selectUserByEmails(emails : String) : User?
    fun selectUserByNDI(ndi : String) : User?
    fun insertUser(user : User) : User
}