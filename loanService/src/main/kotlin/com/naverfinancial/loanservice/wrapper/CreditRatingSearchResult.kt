package com.naverfinancial.loanservice.wrapper

data class CreditRatingSearchResult(
    val ndi: String,
    val grade: Int,
    val isPermit: Boolean,
)