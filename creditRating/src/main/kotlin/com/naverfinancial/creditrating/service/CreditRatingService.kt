package com.naverfinancial.creditrating.service

import com.naverfinancial.creditrating.datasource.creditRatingSearch.dto.CreditRatingSearchResult
import com.naverfinancial.creditrating.datasource.user.dto.User

interface CreditRatingService {
    // 해당하는 ndi에 데이터가 존재하는 지 확인
    fun findCreditRatingSearchResultByNdi(ndi: String) : CreditRatingSearchResult?

    // CB 서버에서 등급을 받아오기
    fun selectGrade(user: User): CreditRatingSearchResult

    // CB 서버에서 등급을 저장하기
    fun saveGrade(ndi : String, grade : Int, isPermit : Boolean): CreditRatingSearchResult

    // 대출 가능 여부를 판단하기
    fun evaluateLoanAvailability(grade: Int): Boolean
}
