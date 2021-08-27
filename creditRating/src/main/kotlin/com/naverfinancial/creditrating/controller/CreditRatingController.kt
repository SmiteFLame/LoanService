package com.naverfinancial.creditrating.controller

import com.naverfinancial.creditrating.entity.creditRatingSearch.dto.CreditRatingSearchResult
import com.naverfinancial.creditrating.exception.NullNdiException
import com.naverfinancial.creditrating.exception.NullUserException
import com.naverfinancial.creditrating.exception.UserException
import com.naverfinancial.creditrating.service.CreditRatingService
import com.naverfinancial.creditrating.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.*
import java.net.ConnectException
import java.net.http.HttpTimeoutException

@RestController
@RequestMapping("/credits")
class CreditRatingController {

    @Autowired
    lateinit var creditRatingService: CreditRatingService

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
                message = "CB서버의 제한시간이 초과되었습니다."
                status = HttpStatus.GATEWAY_TIMEOUT
            }
            is ConnectException ->{
                message = "CB서버가 열리지 않았습니다."
                status = HttpStatus.INTERNAL_SERVER_ERROR
            }
        }

        return ResponseEntity<String>(message, status)
    }

    /**
     * NDI을 입력받아서 유저 정보를 가져온 후 신용등급 및 대출 가능 여부 파악한다
     *
     * RequestBody : ndi : String
     * ResponseEntity : creditResult(신용등급, 대출 가능 여부)
     * BAD_REQUEST - ndi가 RequestBody에 없을 경우
     * NOT_FOUND - User에 해당되는 ndi가 없는 경우
     * GATEWAY_TIMEOUT - 10초 이내로 데이터 요청을 신용등급을 못 가져온 경우
     */
    @PostMapping
    fun selectGrade(@RequestBody map: Map<String, String>): ResponseEntity<CreditRatingSearchResult> {
        if (!map.containsKey("ndi")) {
            throw NullNdiException()
        }

        var user = userService.selectUserByNDI(map.getValue("ndi"))
        if (user == null) {
            throw NullUserException()
        }
        return ResponseEntity<CreditRatingSearchResult>(creditRatingService.selectGrade(user), HttpStatus.OK)
    }
}
