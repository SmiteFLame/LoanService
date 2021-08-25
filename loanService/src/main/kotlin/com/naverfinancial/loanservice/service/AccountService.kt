package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.entity.account.dto.Account
import com.naverfinancial.loanservice.wrapper.CreditResult
import java.util.*

interface AccountService {
    // 마이너스 통장 전체 조회
    fun selectAccountList(page : Int, size : Int): List<Account>

    // 마이너스 통장 계좌 번호 통장 조회
    fun selectAccountByAccountId(accountId: Int): Account?

    // 마이너스 통장 유효아이디 통장 조회 (Page,
    fun selectAccountListByNdi(ndi: String, page : Int, size : Int): List<Account>

    // 노멀 마이너스 통장 유효아이디 통장 조회
    fun selectAccountByNdiStatusNormal(ndi: String) : Account?

    // 마이너스 통장 신청
    fun openAccount(ndi: String, creditResult: CreditResult): Account

    // 대출 신청
    fun depositLoan(accountId: Int, amount: Int): Account

    // 대출 반환
    fun withdrawLoan(accountId: Int, amount: Int): Account

    // 통장 해지
    fun removeAccount(accountId : Int): Integer

    // 등급 조회하기
    fun searchGrade(ndi: String): CreditResult
}