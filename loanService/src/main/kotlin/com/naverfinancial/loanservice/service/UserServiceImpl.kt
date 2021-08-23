package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.entity.user.dto.User
import com.naverfinancial.loanservice.entity.user.repository.UserRepository
import com.naverfinancial.loanservice.wrapper.Register
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

    override fun searchUserByEmails(email: String): User? {
        return userRepository.findUserByEmail(email)
    }

    override fun searchUserByNDI(ndi: String): User? {
        return userRepository.findUserByNdi(ndi)
    }

    override fun saveUser(register: Register): User {
        if(register.user_name == null || register.age == null || register.salary == null || register.email == null || !checkEmailValid(register.email)){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val status = userTransactionManager.getTransaction(DefaultTransactionDefinition())

        val uuid = UUID.randomUUID().toString()
        val user = User(
            ndi = uuid,
            email = register.email,
            userName = register.user_name,
            age = register.age,
            salary = register.salary
        )

        var newUser = userRepository.save(user)

        userTransactionManager.commit(status)

        return newUser
    }
    fun checkEmailValid(email: String) : Boolean{
        val regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"
        val pattern = Pattern.compile(regex)
        return pattern.matcher(email).matches()
    }
}