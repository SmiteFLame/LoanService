package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.datasource.account.dto.Account
import com.naverfinancial.loanservice.datasource.user.dto.UserCreditRating
import com.naverfinancial.loanservice.enumclass.AccountTypeStatus
import org.springframework.data.domain.Page

interface AccountService {
    // 통장 아이디 조회
    fun selectAccountByAccountID(accountId: Int) : Account

    // 통장 리스트 조회
    fun selectAccounts(ndi: String?, status: AccountTypeStatus, limit: Int, offset: Long): Page<Account>

    // 마이너스 통장 신청
    fun openAccount(ndi: String, userCreditRating: UserCreditRating): Account

    // 대출 신청
    fun depositLoan(accountId: Int, amount: Int): Account

    // 대출 반환
    fun withdrawLoan(accountId: Int, amount: Int): Account

    // 통장 해지
    fun removeAccount(accountId: Int)
}
