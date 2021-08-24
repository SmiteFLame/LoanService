package com.naverfinancial.loanservice.controller

import com.naverfinancial.loanservice.entity.user.dto.User
import com.naverfinancial.loanservice.service.UserService
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
    /**
     * email을 입력받아서 해당하는 유저를 반환한다.
     *
     * PathVariable : email : String
     * ResponseEntity : User
     * BAD_REQUEST - User에 해당되는 email가 없는 경우
     */
    @GetMapping("{email}/email")
    fun selectUserByEmail(@PathVariable email : String) : ResponseEntity<User>{
        try{
            val user = userService.selectUserByEmails(email)
            if(user == null){
                return ResponseEntity(HttpStatus.NOT_FOUND)
            }
            return ResponseEntity<User>(user, HttpStatus.OK)
        } catch (err : Exception){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * NDI을 입력받아서 유저 정보를 가져온 다
     *
     * PathVariable: ndi : String
     * ResponseEntity : User
     * BAD_REQUEST - User에 해당되는 ndi가 없는 경우
     * GATEWAY_TIMEOUT - 10초 이내로 데이터 요청을 신용등급을 못 가져온 경우
     */
    @GetMapping("{ndi}")
    fun selectUserByNdi(@PathVariable ndi : String) : ResponseEntity<User> {
        try {
            var user = userService.selectUserByNDI(ndi)
            if (user == null) {
                return ResponseEntity(HttpStatus.NOT_FOUND)
            }
            return ResponseEntity<User>(user, HttpStatus.OK)
        } catch (err : Exception){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * 회원가입 정보를 입력 받아서 User를 등록
     *
     * RequestBody : register : Register
     * ResponseEntity : User
     * BAD_REQUEST - 필수 회원 가입 정보가 들어오지 않은 경우, 이메일 형식이 잘못된 경우
     */
    @PostMapping()
    fun insertUser(@RequestBody user : User) : ResponseEntity<User>{
        try {
            // register를 파싱하는 과정에서 오류 -> 400 리턴 추가
            return ResponseEntity<User>(userService.insertUser(user), HttpStatus.CREATED)
        } catch (err : Exception){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}