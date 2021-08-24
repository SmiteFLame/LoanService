package com.naverfinancial.creditrating.service

import com.naverfinancial.creditrating.entity.creditRatingSearch.dto.CreditRatingSearchResult

interface MainService {
    fun selectGrade(ndi : String) : CreditRatingSearchResult
    fun evaluateLoanAvailability(grade: Int): Boolean
}