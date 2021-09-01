package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.datasource.account.dto.Account
import com.naverfinancial.loanservice.datasource.account.dto.AccountTransactionHistory
import com.naverfinancial.loanservice.datasource.user.dto.UserCreditRating

interface AccountService {
    // 마이너스 통장 신청
    fun openAccount(ndi: String, userCreditRating: UserCreditRating): Account

    // 대출 신청
    fun depositLoan(account: Account, amount: Int): Account

    // 대출 반환
    fun withdrawLoan(account: Account, amount: Int): Account

    // 통장 해지
    fun removeAccount(account: Account): Integer
}
