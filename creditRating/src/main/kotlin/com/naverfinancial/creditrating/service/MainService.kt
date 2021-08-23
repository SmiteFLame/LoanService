package com.naverfinancial.creditrating.service

import com.naverfinancial.creditrating.wrapper.CreditResult

interface MainService {
    fun selectGrade(ndi : String) : CreditResult
    fun evaluateLoanAvailability(grade: Int): Boolean
}