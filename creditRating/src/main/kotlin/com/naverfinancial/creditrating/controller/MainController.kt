package com.naverfinancial.creditrating.controller

import com.naverfinancial.creditrating.service.MainService
import com.naverfinancial.creditrating.wrapper.CreditResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.http.HttpTimeoutException
import java.util.concurrent.TimeoutException

@RestController
@CrossOrigin("*")
@RequestMapping("/credits")
class MainController {


    @Autowired
    lateinit var mService : MainService

    /**
     * NDI을 입력받아서 유저 정보를 가져온 후 신용등급 및 대출 가능 여부 파악한다
     *
     * RequestBody : ndi : String
     * ResponseEntity : creditResult(신용등급, 대출 가능 여부)
     * BAD_REQUEST - ndi가 RequestBody에 없을 경우, User에 해당되는 ndi가 없는 경우
     * GATEWAY_TIMEOUT - 10초 이내로 데이터 요청을 신용등급을 못 가져온 경우
     */
    @PostMapping
    fun selectGrade(@RequestBody map : Map<String,String>): ResponseEntity<CreditResult>{
        try {
            if (!map.containsKey("ndi")) {
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }
            return ResponseEntity<CreditResult>(mService.selectGrade(map.getValue("ndi")), HttpStatus.CREATED)
        } catch (err : HttpTimeoutException){
            return ResponseEntity(HttpStatus.GATEWAY_TIMEOUT)
        } catch (err : Exception){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }

    }
}