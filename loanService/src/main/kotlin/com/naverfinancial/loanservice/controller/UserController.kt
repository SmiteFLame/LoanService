package com.naverfinancial.loanservice.controller

import com.naverfinancial.loanservice.dto.User
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
    fun searchUserByEmail(@PathVariable email : String) : ResponseEntity<Optional<User>>{
        try{
            val user = userService.searchUserByEmails(email)
            if(user.isEmpty){
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }
            return ResponseEntity<Optional<User>>(user, HttpStatus.OK)
        } catch (err : Exception){
            return ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
        }
    }

    @GetMapping("{NDI}")
    fun searchUserByNDI(@PathVariable NDI : String) : ResponseEntity<Optional<User>> {
        try {
            var user = userService.searchUserByNDI(NDI)
            if (user.isEmpty) {
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }
            return ResponseEntity<Optional<User>>(user, HttpStatus.OK)
        } catch (err : Exception){
            return ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
        }
    }

    @PostMapping()
    fun saveUser(@RequestBody register: Register) : ResponseEntity<User>{
        try {
            // register 오류 1차 검사 진행하기
            return ResponseEntity<User>(userService.saveUser(register), HttpStatus.CREATED)
        } catch (err : Exception){
            return ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
        }
    }
}