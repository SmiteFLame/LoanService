package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.datasource.user.dto.User
import com.naverfinancial.loanservice.datasource.user.dto.UserCreditRating
import com.naverfinancial.loanservice.wrapper.CreditRatingSearchResult

interface UserService {
    // 사용자 신용 등급 조회
    fun searchCreditRating(ndi: String): UserCreditRating

    // 사용자 신용 등급 저장
    fun saveCreditRating(ndi: String, creditRatingSearchResult: CreditRatingSearchResult): UserCreditRating

    // 사용자 추가하기
    fun insertUser(user: User): User

    // 등급 조회하기
    fun searchGrade(ndi: String): CreditRatingSearchResult
}
