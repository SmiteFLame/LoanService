package com.naverfinancial.loanservice.dto

import java.sql.Timestamp
import javax.persistence.*

@Table(name="account_transaction_historys")
@Entity
data class AccountTransactionHistory (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="history_id")
    val historyId : Int = 0,

    @Column(nullable = false)
    val amount : Int,

    @Column(nullable = false)
    val type : String,

    @Column(name="created_date",nullable = false)
    val createdDate : Timestamp,

    @Column(name="account_id", nullable = false)
    val accountId : Int,

    @Column(name="account_numbers", nullable = false)
    val accountNumbers : String,
)