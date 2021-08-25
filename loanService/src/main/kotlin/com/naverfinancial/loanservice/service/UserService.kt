package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.entity.user.dto.User
import com.naverfinancial.loanservice.entity.user.dto.UserCreditRating
import com.naverfinancial.loanservice.wrapper.CreditRatingSearchResult

interface UserService {
    // 사용자 이메일 조회
    fun selectUserByEmails(emails : String) : User?

    // 사용자 NDI 조회
    fun selectUserByNDI(ndi : String) : User?

    // 사용자 신용 등급 조회
    fun selectCreditRating(ndi : String) : UserCreditRating?

    // 사용자 신용 등급 생성
    fun saveCreditRating(ndi : String) : UserCreditRating

    // 사용자 추가하기
    fun insertUser(user : User) : User

    // 등급 조회하기
    fun searchGrade(ndi: String): CreditRatingSearchResult
}