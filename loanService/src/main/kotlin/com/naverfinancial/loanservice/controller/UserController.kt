package com.naverfinancial.loanservice.controller

import com.naverfinancial.loanservice.dto.User
import com.naverfinancial.loanservice.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/users")
class UserController{

    @Autowired
    lateinit var userService : UserService

    @GetMapping("{email}/email")
    fun searchEmail(@PathVariable email : String) : ResponseEntity<Optional<User>>{
        val user = userService.searchUserByEmails(email)
        if(user.isEmpty()){
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity<Optional<User>>(user, HttpStatus.OK)
    }
}