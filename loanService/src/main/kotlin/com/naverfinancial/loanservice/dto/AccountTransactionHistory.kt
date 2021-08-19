package com.naverfinancial.loanservice.dto

import java.sql.Timestamp
import javax.persistence.*

@Table(name="account_transaction_historys")
@Entity
data class AccountTransactionHistory (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="history_id")
    private var historyId : Int = 0,

    @Column(nullable = false)
    private var amount : Int,

    @Column(nullable = false)
    private var type : String,

    @Column(name="created_date",nullable = false)
    private var createdDate : Timestamp,

    @Column(name="account_id", nullable = false)
    var accountId : Int,

    @Column(name="account_numbers", nullable = false)
    var accountNumbers : String,

/*    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_id")
    private var account: Account*/
)