package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.datasource.account.dto.Account
import com.naverfinancial.loanservice.datasource.account.dto.AccountTransactionHistory
import com.naverfinancial.loanservice.datasource.user.dto.UserCreditRating

interface AccountService {
    // 마이너스 통장 전체 조회
    fun selectAccountList(limit : Int, offset : Int): List<Account>

    // 마이너스 통장 계좌 번호 통장 조회
    fun selectAccountByAccountId(accountId: Int): Account?

    // 마이너스 통장 유효아이디 통장 조회 (Page,
    fun selectAccountListByNdi(ndi: String, limit : Int, offset : Int): List<Account>

    // 노멀 마이너스 통장 유효아이디 통장 조회
    fun selectAccountByNdiStatusNormal(ndi: String) : Account?

    // 마이너스 통장 신청
    fun openAccount(ndi: String, userCreditRating: UserCreditRating): Account

    // 대출 신청
    fun depositLoan(account: Account, amount: Int): Account

    // 대출 반환
    fun withdrawLoan(account: Account, amount: Int): Account

    // 통장 해지
    fun removeAccount(account : Account): Integer

    // 통장 거래 내역 조회
    fun selectAccountTransactionList(limit : Int, offset : Int) : List<AccountTransactionHistory>

    // 통장 거래 내역 조회
    fun selectAccountTransactionListByAccountId(account : Account, limit : Int, offset : Int) : List<AccountTransactionHistory>

}
