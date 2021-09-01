package com.naverfinancial.creditrating.service

import com.naverfinancial.creditrating.datasource.creditRatingSearch.dto.CreditRatingSearchResult
import com.naverfinancial.creditrating.datasource.user.dto.User

interface CreditRatingService {
    // CB 서버에서 등급을 받아오기
    fun selectGrade(user: User): CreditRatingSearchResult

    // 대출 가능 여부를 판단하기
    fun evaluateLoanAvailability(grade: Int): Boolean
}
