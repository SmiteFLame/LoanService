package com.naverfinancial.loanservice.wrapper

data class CreditResult (
    private var grade : Int,
    private var isPermit : Boolean,
){
    fun getGrade() = grade
    fun getIsPermit() = isPermit
}