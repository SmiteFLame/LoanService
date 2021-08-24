package com.naverfinancial.creditrating.controller

import com.naverfinancial.creditrating.entity.creditRatingSearch.dto.CreditRatingSearchResult
import com.naverfinancial.creditrating.enumClass.ExceptionEnum
import com.naverfinancial.creditrating.service.CreditRatingService
import com.naverfinancial.creditrating.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin("*")
@RequestMapping("/credits")
class CreditRatingController {

    @Autowired
    lateinit var creditRatingService: CreditRatingService

    @Autowired
    lateinit var userService: UserService

    /**
     * NDI을 입력받아서 유저 정보를 가져온 후 신용등급 및 대출 가능 여부 파악한다
     *
     * RequestBody : ndi : String
     * ResponseEntity : creditResult(신용등급, 대출 가능 여부)
     * BAD_REQUEST - ndi가 RequestBody에 없을 경우, User에 해당되는 ndi가 없는 경우
     * GATEWAY_TIMEOUT - 10초 이내로 데이터 요청을 신용등급을 못 가져온 경우
     */
    @ExceptionHandler
    fun exceptionHandler(error : Exception): ResponseEntity<String> {
        // 정의할 수 있는 에러 처리
        when(error.message){
            ExceptionEnum.NOT_FOUND_NDI.toString() -> return ResponseEntity<String>("NDI가 요청되지 않음", HttpStatus.BAD_REQUEST)
            ExceptionEnum.NOT_FOUND_USER.toString() -> return ResponseEntity<String>("USER가 존재하지 않음", HttpStatus.BAD_REQUEST)
        }

        // 정의할 수 없는 에러 처리
        when(error){
            is HttpMessageNotReadableException -> return ResponseEntity<String>("JSON 데이터가 요청되지 않음", HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity<String>("서버 에러", HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PostMapping
    fun selectGrade(@RequestBody map: Map<String, String>): ResponseEntity<CreditRatingSearchResult> {
        println("A")
        if (!map.containsKey("ndi")) {
            throw Exception(ExceptionEnum.NOT_FOUND_NDI.toString())
        }

        var user = userService.selectUserByNDI(map.getValue("ndi"))
        if(user == null){
            throw Exception(ExceptionEnum.NOT_FOUND_USER.toString())
        }
        return ResponseEntity<CreditRatingSearchResult>(creditRatingService.selectGrade(user), HttpStatus.OK)
    }
}