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

    override fun searchUserByEmails(email: String): Optional<User> {
        return userRespository.findUserByEmail(email)
    }

    override fun searchUserByNDI(NDI: String): Optional<User> {
        return userRespository.findUserByNDI(NDI)
    }

    override fun saveUser(register: Register): User {
        var status = userTransactionManager.getTransaction(DefaultTransactionDefinition())

        val uuid = UUID.randomUUID().toString()
        val user = User(
            NDI = uuid,
            email = register.getEmails(),
            userName = register.getUserName(),
            age = register.getAge(),
            salary = register.getSalary()
        )

        var newUser = userRespository.save(user)

        userTransactionManager.commit(status)

        return newUser
    }
}