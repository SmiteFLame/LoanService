package com.naverfinancial.loanservice.wrapper

import com.naverfinancial.loanservice.enumclass.AccountRequestTypeStatus

data class ApplymentLoanService (
    val type: AccountRequestTypeStatus,
    val amount: Int
)
