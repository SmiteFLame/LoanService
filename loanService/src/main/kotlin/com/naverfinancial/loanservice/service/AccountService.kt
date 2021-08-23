package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.entity.account.dto.Account
import com.naverfinancial.loanservice.wrapper.CreditResult
import java.util.*

interface AccountService {
    // 마이너스 통장 전체 조회
    fun searchAll(): List<Account>

    // 마이너스 통장 계좌 번호 통장 조회
    fun searchByAccountNumbers(accountNumbers: String): Account?

    // 마이너스 통장 유효아이디 통장 조회
    fun searchByNdi(ndi: String): List<Account>

    // 마이너스 통장 신청
    fun openAccount(ndi: String, creditResult: CreditResult): Account

    // 대출 신청
    fun depositLoan(accountNumbers: String, amount: Int): Account

    // 대출 반환
    fun withdrawLoan(accountNumbers: String, amount: Int): Account

    // 통장 해지
    fun cancelAccount(accountNumbers: String): Integer

    // 등급 조회하기
    fun searchGrade(ndi: String): CreditResult
}