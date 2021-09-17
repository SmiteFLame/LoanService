package com.naverfinancial.loanservice.cache

import com.naverfinancial.loanservice.datasource.user.dto.UserCreditRating
import com.naverfinancial.loanservice.wrapper.LRUCache

object LoanServiceCache {
    val userCreditRatingCache = LRUCache<String, UserCreditRating>(3, 1000)
}
