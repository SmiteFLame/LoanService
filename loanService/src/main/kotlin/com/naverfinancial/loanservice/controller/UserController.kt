package com.naverfinancial.loanservice.controller

import com.naverfinancial.loanservice.datasource.user.dto.User
import com.naverfinancial.loanservice.datasource.user.dto.UserCreditRating
import com.naverfinancial.loanservice.exception.UserException
import com.naverfinancial.loanservice.service.UserService
import com.naverfinancial.loanservice.utils.EmailValiation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.ConnectException
import java.net.http.HttpTimeoutException

@RestController
@RequestMapping("/users")
class UserController {

    @Autowired
    lateinit var userService: UserService

    @ExceptionHandler(UserException::class)
    fun userExceptionHandler(error: UserException): ResponseEntity<String> {
        return ResponseEntity<String>(error.message, error.status)
    }

    @ExceptionHandler
    fun exceptionHandler(error: Exception): ResponseEntity<String> {
        var message = error.message
        var status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        when (error) {
            is HttpMessageNotReadableException -> {
                message = "입력값이 잘못 들어왔습니다"
                status = HttpStatus.BAD_REQUEST
            }
            is HttpTimeoutException -> {
                message = "제한시간이 초과되었습니다."
                status = HttpStatus.GATEWAY_TIMEOUT
            }
            is ConnectException -> {
                message = "신용 등급 서버가 열리지 않았습니다."
                status = HttpStatus.INTERNAL_SERVER_ERROR
            }
        }

        return ResponseEntity<String>(message, status)
    }

    /**
     * email을 입력받아서 해당하는 유저를 반환한다.
     *
     * PathVariable : email : String
     * ResponseEntity : User
     * NOT_FOUND - User에 해당되는 email가 없는 경우
     */
    @GetMapping("{email}/email")
    fun selectUserByEmail(@PathVariable email: String): ResponseEntity<User> {

        val user = userService.selectUserByEmails(email) ?: throw UserException.NullUserException(HttpStatus.OK)
        return ResponseEntity<User>(user, HttpStatus.OK)

    }

    /**
     * NDI을 입력받아서 유저 정보를 가져온 후 출력
     *
     * PathVariable: ndi : String
     * ResponseEntity : User
     * BAD_REQUEST - ndi가 없이 요청된 경우
     * NOT_FOUND - User에 해당되는 ndi가 없는 경우
     */
    @GetMapping("{ndi}")
    fun selectUserByNdi(@PathVariable ndi: String?): ResponseEntity<User> {
        if (ndi == null || ndi == "") {
            throw UserException.NullNdiException()
        }
        var user: User? = userService.selectUserByNDI(ndi) ?: throw UserException.NullUserException(HttpStatus.OK)
        return ResponseEntity<User>(user, HttpStatus.OK)

    }

    /**
     * 사용자의 신용등급 조회를 신청
     *
     * PathVariable: ndi : String
     * ResponseEntity : User
     * BAD_REQUEST - ndi가 없이 요청된 경우
     * NOT_FOUND - User에 해당되는 ndi가 없는 경우
     */
    @GetMapping("/credit/{ndi}")
    fun selectCreditRating(@PathVariable ndi: String): ResponseEntity<UserCreditRating> {
        if (ndi == null || ndi == "") {
            throw UserException.NullNdiException()
        }
        return ResponseEntity<UserCreditRating>(userService.saveCreditRating(ndi), HttpStatus.OK)
    }

    /**
     * 회원가입 정보를 입력 받아서 User를 등록
     *
     * RequestBody : user : User
     * ResponseEntity : User
     * BAD_REQUEST - ndi가 없이 요청된 경우, 회원가입 정보가 잘못 들어온 경우
     * NOT_FOUND - User에 해당되는 ndi가 없는 경우
     */
    @PostMapping()
    fun insertUser(@RequestBody user: User?): ResponseEntity<User> {
        if (user == null || !EmailValiation.checkEmailValid(user.email)) {
            throw UserException.InvalidUserException()
        }
        if (userService.selectUserByEmails(user.email) != null) {
            throw UserException.DuplicationEmailException()
        }
        return ResponseEntity<User>(userService.insertUser(user), HttpStatus.OK)
    }
}
