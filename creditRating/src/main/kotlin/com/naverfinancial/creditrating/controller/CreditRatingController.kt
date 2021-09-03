package com.naverfinancial.creditrating.controller

import com.naverfinancial.creditrating.datasource.creditRatingSearch.dto.CreditRatingSearchResult
import com.naverfinancial.creditrating.datasource.user.repository.UserRepository
import com.naverfinancial.creditrating.exception.UserException
import com.naverfinancial.creditrating.service.CreditRatingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/credits")
class CreditRatingController {

    @Autowired
    lateinit var creditRatingService: CreditRatingService

    @Autowired
    lateinit var userRepository: UserRepository

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
            throw UserException.NullNdiException()
        }

        val user = userRepository.findUserByNdi(map.getValue("ndi")) ?: throw UserException.NullUserException()
        return ResponseEntity<CreditRatingSearchResult>(creditRatingService.selectGrade(user), HttpStatus.OK)
    }
}
