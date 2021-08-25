package com.naverfinancial.loanservice.wrapper

import com.naverfinancial.loanservice.enumclass.AccountRequestTypeStatus

data class Detail (
    val type: AccountRequestTypeStatus,
    val amount: Int
)