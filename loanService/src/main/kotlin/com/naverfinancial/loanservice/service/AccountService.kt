package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.dto.Account
import com.naverfinancial.loanservice.dto.User
import java.util.*

interface AccountService {
    // 마이너스 통장 전체 조회
    fun searchAll() : List<Account>

    // 마이너스 통장 계좌 번호 통장 조회
    fun searchByAccountNumbers(account_numbers : String) : Account

    // 마이너스 통장 유효아이디 통장 조회
    fun searchByNDI(NDI:String) : Account

    // 마이너스 통장 신청
    fun openAccount(user : User) : Account

    // 대출 신청
    fun depositLoan(account_numbers: String, amount : Int) : Account

    // 대출 반환
    fun withdrawLoan(account_numbers: String, amount : Int) : Account

    // 통장 해지
    fun cancelAccount(account_numbers: String)

}