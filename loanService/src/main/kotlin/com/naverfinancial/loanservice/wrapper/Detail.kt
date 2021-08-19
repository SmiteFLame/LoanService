package com.naverfinancial.loanservice.wrapper

class Detail {
    private var type: String
    private var amount: Int

    constructor(type: String, amount: Int) {
        this.type = type
        this.amount = amount
    }

    fun getType() = type
    fun getAmount() = amount

}