package com.naverfinancial.creditrating.service

import com.naverfinancial.creditrating.wrapper.CreditResult

interface MainService {
    fun selectGrade(NDI : String) : CreditResult
    fun evaluateLoanAvailability(grade: Int): Boolean
}