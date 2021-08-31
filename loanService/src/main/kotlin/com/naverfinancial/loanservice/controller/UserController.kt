package com.naverfinancial.loanservice.controller

import com.naverfinancial.loanservice.datasource.user.dto.User
import com.naverfinancial.loanservice.datasource.user.dto.UserCreditRating
import com.naverfinancial.loanservice.datasource.user.repository.UserRepository
import com.naverfinancial.loanservice.exception.UserException
import com.naverfinancial.loanservice.service.UserService
import com.naverfinancial.loanservice.utils.EmailValiation
import com.naverfinancial.loanservice.utils.PagingUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.ConnectException
import java.net.http.HttpTimeoutException

@RestController
@RequestMapping("/users")
class UserController {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userRepository: UserRepository

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
                message = "입력값이 잘못 들어왔습니다."
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
     * 모든 유저 정보를 가져온 후 출력
     *
     * RequestParam: limit : Int, offset : Int
     * ResponseEntity : User
     * BAD_REQUEST : limit, offset가 잘못 설정된 경우
     * NOT_FOUND : 조건에 맞는 유저가 없는 경우
     */
    @GetMapping()
    fun selectUsers( @RequestParam limit: Int, offset: Int): ResponseEntity<Page<User>> {
        PagingUtil.checkIsValid(limit, offset)
        var users = userRepository.findAll(PageRequest.of(PagingUtil.getPage(limit, offset), limit))
        if(!users.hasContent()){
            throw UserException.NullUserException()
        }
        return ResponseEntity<Page<User>>(users, HttpStatus.OK)
    }

    /**
     * NDI(Email)을 입력받아서 유저 정보를 가져온 후 출력
     *
     * PathVariable: word : String
     * RequestParam: idType : String?
     * ResponseEntity : User
     * BAD_REQUEST - id-type이 잘못 들어온 경우
     * NOT_FOUND - NDI(Email)에 해당하는 유저가 없는 경우
     */
    @GetMapping("{word}")
    fun selectUserByNdi(@PathVariable word: String, @RequestParam("id-type") idType : String?): ResponseEntity<User> {
        var user: User? = if(idType == "email"){
            userRepository.findUserByEmail(word)
        } else if(idType != null){
            throw UserException.NonIdTypeException()
        } else{
            userRepository.findUserByNdi(word)
        }
        if(user == null){
            throw UserException.NullUserException()
        }
        return ResponseEntity<User>(user, HttpStatus.OK)
    }

    /**
     * 회원가입 정보를 입력 받아서 User를 등록
     *
     * RequestBody : user : User
     * ResponseEntity : User
     * BAD_REQUEST - 값이 일부 혹은 전부가 입력되지 않은 경우, 이메일의 조건이 맞지 않는 경우, 이메일이 이미 존재하는 경우
     * NOT_FOUND - User에 해당되는 ndi가 없는 경우
     */
    @PostMapping()
    fun insertUser(@RequestBody user: User?): ResponseEntity<User> {
        if (user == null) {
            throw UserException.InvalidUserException()
        }
        if (!EmailValiation.checkEmailValid(user.email)) {
            throw UserException.InvalidEmailException()
        }
        if (userRepository.findUserByEmail(user.email) != null) {
            throw UserException.DuplicationEmailException()
        }
        return ResponseEntity<User>(userService.insertUser(user), HttpStatus.CREATED)
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
        userRepository.findUserByNdi(ndi) ?: throw UserException.NullUserException()
        return ResponseEntity<UserCreditRating>(userService.saveCreditRating(ndi), HttpStatus.CREATED)
    }

}
