package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.datasource.account.dto.Account
import com.naverfinancial.loanservice.datasource.user.dto.UserCreditRating
import com.naverfinancial.loanservice.enumclass.AccountTypeStatus
import org.springframework.data.domain.Page

interface AccountService {
    // 통장 리스트 조회
    fun selectAccounts(ndi: String?, status : AccountTypeStatus, limit : Int, offset : Int) : Page<Account>

    // 마이너스 통장 신청
    fun openAccount(ndi: String, userCreditRating: UserCreditRating): Account

    // 대출 신청
    fun depositLoan(account: Account, amount: Int): Account

    // 대출 반환
    fun withdrawLoan(account: Account, amount: Int): Account

    // 통장 해지
    fun removeAccount(account: Account): Int
}
