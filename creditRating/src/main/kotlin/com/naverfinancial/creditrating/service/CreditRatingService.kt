package com.naverfinancial.creditrating.service

import com.naverfinancial.creditrating.entity.creditRatingSearch.dto.CreditRatingSearchResult
import com.naverfinancial.creditrating.entity.user.dto.User

interface CreditRatingService {
    fun selectGrade(user : User) : CreditRatingSearchResult
    fun evaluateLoanAvailability(grade: Int): Boolean
}