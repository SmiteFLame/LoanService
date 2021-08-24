package com.naverfinancial.creditrating.controller

import com.naverfinancial.creditrating.entity.creditRatingSearch.dto.CreditRatingSearchResult
import com.naverfinancial.creditrating.enumClass.ExceptionEnum
import com.naverfinancial.creditrating.exception.NullNdiException
import com.naverfinancial.creditrating.exception.NullUserException
import com.naverfinancial.creditrating.service.CreditRatingService
import com.naverfinancial.creditrating.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.*
import java.net.http.HttpTimeoutException

@RestController
@CrossOrigin("*")
@RequestMapping("/credits")
class CreditRatingController {

    @Autowired
    lateinit var creditRatingService: CreditRatingService

    @Autowired
    lateinit var userService: UserService

    @ExceptionHandler
    fun exceptionHandler(error: Exception): ResponseEntity<Exception> {
        when (error) {
            is NullNdiException -> return ResponseEntity<Exception>(
                error,
                HttpStatus.BAD_REQUEST
            )
            is NullUserException -> return ResponseEntity<Exception>(
                error,
                HttpStatus.BAD_REQUEST
            )
            is HttpTimeoutException -> return ResponseEntity<Exception>(
                error,
                HttpStatus.GATEWAY_TIMEOUT
            )
            is HttpMessageNotReadableException -> return ResponseEntity<Exception>(
                error,
                HttpStatus.BAD_REQUEST
            )
        }

        // 그외 에러들
        return ResponseEntity<Exception>(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    /**
     * NDI을 입력받아서 유저 정보를 가져온 후 신용등급 및 대출 가능 여부 파악한다
     *
     * RequestBody : ndi : String
     * ResponseEntity : creditResult(신용등급, 대출 가능 여부)
     * BAD_REQUEST - ndi가 RequestBody에 없을 경우, User에 해당되는 ndi가 없는 경우
     * GATEWAY_TIMEOUT - 10초 이내로 데이터 요청을 신용등급을 못 가져온 경우
     */
    @PostMapping
    fun selectGrade(@RequestBody map: Map<String, String>): ResponseEntity<CreditRatingSearchResult> {
        if (!map.containsKey("ndi")) {
            throw NullPointerException("ndi")
        }

        var user = userService.selectUserByNDI(map.getValue("ndi"))
        if (user == null) {
            throw NullPointerException("user")
        }
        return ResponseEntity<CreditRatingSearchResult>(creditRatingService.selectGrade(user), HttpStatus.OK)
    }
}