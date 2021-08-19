package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.dto.User

interface UserService {
    fun searchUserByEmails() : User
    fun searchUserByNDI() : User
    fun saveUser() : User
}