package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.dto.User
import com.naverfinancial.loanservice.repository.UserRespository
import com.naverfinancial.loanservice.wrapper.Register
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServiceImpl : UserService {

    @Autowired
    lateinit var userRespository: UserRespository

    override fun searchUserByEmails(email: String): Optional<User> {
        return userRespository.findUserByEmail(email)
    }

    override fun searchUserByNDI(NDI: String): User {
        return userRespository.findUserByNDI(NDI)
    }

    override fun saveUser(register: Register): User {
        var uuid = UUID.randomUUID().toString()
        var user = User(
            NDI = uuid,
            email = register.getEmails(),
            user_name = register.getUserName(),
            age = register.getAge(),
            salary = register.getSalary()
        )
        return userRespository.save(user)
    }
}