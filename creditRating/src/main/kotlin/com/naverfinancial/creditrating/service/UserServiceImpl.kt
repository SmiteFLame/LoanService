package com.naverfinancial.creditrating.service

import com.naverfinancial.creditrating.entity.user.dto.User
import com.naverfinancial.creditrating.entity.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserServiceImpl : UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    override fun selectUserByNDI(ndi: String): User? {
        return userRepository.findUserByNdi(ndi)
    }
}