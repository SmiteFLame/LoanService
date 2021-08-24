package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.entity.user.dto.User
import com.naverfinancial.loanservice.entity.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition
import org.springframework.web.server.ResponseStatusException
import java.util.*
import java.util.regex.Pattern

@Service
class UserServiceImpl : UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Qualifier("user")
    @Autowired
    lateinit var userTransactionManager : PlatformTransactionManager

    override fun selectUserByEmails(email: String): User? {
        return userRepository.findUserByEmail(email)
    }

    override fun selectUserByNDI(ndi: String): User? {
        return userRepository.findUserByNdi(ndi)
    }

    override fun insertUser(user: User): User {
        val status = userTransactionManager.getTransaction(DefaultTransactionDefinition())

        val uuid = UUID.randomUUID().toString()
        user.ndi = uuid

        userRepository.save(user)

        userTransactionManager.commit(status)

        return user
    }
}