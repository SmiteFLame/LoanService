package com.naverfinancial.loanservice.dto

import java.sql.Timestamp
import javax.persistence.*

@Table(name="accounts")
@Entity
data class Account (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var account_id : Int,

        @Column(nullable = false, unique = true)
        var account_number : String,

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

        @Column(nullable = false)
        var created_date : Timestamp,

        var lona_start_date : Timestamp,
)