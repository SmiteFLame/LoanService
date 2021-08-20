package com.naverfinancial.creditrating.creditRatingSearch.service

import com.naverfinancial.creditrating.wrapper.CreditResult

interface MainService {
    fun selectGrade(NDI : String) : CreditResult
    fun evaluateLoanAvailability(grade: Int): Boolean
}