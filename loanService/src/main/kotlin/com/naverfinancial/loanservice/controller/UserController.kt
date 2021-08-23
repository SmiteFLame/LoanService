package com.naverfinancial.loanservice.controller

import com.naverfinancial.loanservice.entity.user.dto.User
import com.naverfinancial.loanservice.service.UserService
import com.naverfinancial.loanservice.wrapper.Register
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/users")
class UserController{

    @Autowired
    lateinit var userService : UserService

    @GetMapping("{email}/email")
    fun searchUserByEmail(@PathVariable email : String) : ResponseEntity<User>{
        try{
            val user = userService.searchUserByEmails(email)
            if(user == null){
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }
            return ResponseEntity<User>(user, HttpStatus.OK)
        } catch (err : Exception){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("{ndi}")
    fun searchUserByNdi(@PathVariable ndi : String) : ResponseEntity<User> {
        try {
            var user = userService.searchUserByNDI(ndi)
            if (user == null) {
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }
            return ResponseEntity<User>(user, HttpStatus.OK)
        } catch (err : Exception){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PostMapping()
    fun saveUser(@RequestBody register: Register) : ResponseEntity<User>{
        try {
            // register 오류 1차 검사 진행하기 -> BAD_REQUEST
            return ResponseEntity<User>(userService.saveUser(register), HttpStatus.CREATED)
        } catch (err : Exception){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}