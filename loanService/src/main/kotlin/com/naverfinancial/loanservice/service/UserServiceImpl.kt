package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.entity.user.dto.User
import com.naverfinancial.loanservice.entity.user.repository.UserRespository
import com.naverfinancial.loanservice.wrapper.Register
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.DefaultTransactionDefinition
import java.util.*

@Service
class UserServiceImpl : UserService {

    @Autowired
    lateinit var userRespository: UserRespository

    @Qualifier("user")
    @Autowired
    lateinit var userTransactionManager : PlatformTransactionManager

    override fun searchUserByEmails(email: String): User? {
        return userRespository.findUserByEmail(email)
    }

    override fun searchUserByNDI(ndi: String): User? {
        return userRespository.findUserByNdi(ndi)
    }

    override fun saveUser(register: Register): User {
        val status = userTransactionManager.getTransaction(DefaultTransactionDefinition())

        val uuid = UUID.randomUUID().toString()
        val user = User(
            ndi = uuid,
            email = register.emails,
            userName = register.user_name,
            age = register.age,
            salary = register.salary
        )

        var newUser = userRespository.save(user)

        userTransactionManager.commit(status)

        return newUser
    }
}