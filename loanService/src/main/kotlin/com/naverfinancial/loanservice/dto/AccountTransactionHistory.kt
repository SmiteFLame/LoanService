package com.naverfinancial.loanservice.dto

import java.sql.Timestamp
import javax.persistence.*

@Table(name="account_transaction_historys")
@Entity
data class AccountTransactionHistorys (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var account_transaction_historys_id : Int = 0,

    @Column(nullable = false)
    private var amount : Int,

    @Column(nullable = false)
    private var deal_request_date : Timestamp,

    @Column(nullable = false)
    private var type : String,

    @Column(nullable = false)
    var account_id : Int,

    @Column(nullable = false)
    var account_number : String,

/*    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_id")
    private var account: Account*/
)