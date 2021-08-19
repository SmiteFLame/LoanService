package com.naverfinancial.loanservice.dto

import java.sql.Timestamp
import javax.persistence.*

@Table(name="accounts")
@Entity
data class Account (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="account_id")
        private var accountId : Int,

        @Column(name="account_numbers", nullable = false, unique = true)
        private var accountNumbers : String,

        @Column(nullable = false)
        private var NDI : String,

        @Column(name="loan_limit", nullable = false)
        private var loanLimit :Int,

        @Column(nullable = false)
        private var balance :Int,

        @Column(nullable = false)
        private var grade :Int,

        @Column(nullable = false)
        private var status : String,

        @Column(name="created_date", nullable = false)
        private var createdDate : Timestamp,

){
        @Column(name="loan_start_date")
        private lateinit var loanStartDate : Timestamp

        fun getAccountID() = accountId
        fun getAccountNumbers() = accountNumbers
        fun getNDI() = NDI
        fun getLoanLimit() = loanLimit
        fun getGrade() = grade
        fun getStatus() = status
        fun getCreatedDate() = createdDate
        fun getLoanStartDate() = loanStartDate
}