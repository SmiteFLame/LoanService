package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.dto.User
import com.naverfinancial.loanservice.repository.UserRespository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserServiceImpl : UserService{

    @Autowired
    lateinit var userRespository: UserRespository

    override fun searchUserByEmails(): User {
        TODO("Not yet implemented")
    }

    override fun searchUserByNDI(): User {
        TODO("Not yet implemented")
    }

    override fun saveUser(): User {
        TODO("Not yet implemented")
    }
}