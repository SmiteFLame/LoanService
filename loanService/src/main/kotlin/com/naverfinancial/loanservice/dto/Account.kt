package com.naverfinancial.loanservice.dto

import java.sql.Timestamp
import javax.persistence.*

@Table(name="accounts")
@Entity
data class Account (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="account_id")
        var accountId : Int,

        @Column(name="account_numbers", nullable = false, unique = true)
        var accountNumbers : String,

        @Column(nullable = false)
        var NDI : String,

        @Column(nullable = false)
        var loan_limit :Int,

        @Column(nullable = false)
        var balance :Int,

        @Column(nullable = false)
        var grade :Int,

        @Column(nullable = false)
        var status : String,

        @Column(name="created_date", nullable = false)
        var createdDate : Timestamp,

        @Column(name="loan_start_date")
        var loanStartDate : Timestamp,
)