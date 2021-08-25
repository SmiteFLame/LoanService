package com.naverfinancial.loanservice.entity.account.dto

import com.naverfinancial.loanservice.enumclass.AccountRequestTypeStatus
import java.sql.Timestamp
import javax.persistence.*

@Table(name="account_transaction_history")
@Entity
data class AccountTransactionHistory (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="history_id")
    val historyId : Int = 0,

    @Column(nullable = false)
    val amount : Int,

    @Column(nullable = false)
    val type : AccountRequestTypeStatus,

    @Column(name="translated_date",nullable = false)
    val translatedDate : Timestamp,

    @Column(name="account_id", nullable = false)
    val accountId : Int,

    @Column(name="account_number", nullable = false)
    val accountNumber : String,
)