package com.naverfinancial.loanservice.entity.account.dto

import java.sql.Timestamp
import javax.persistence.*

@Table(name="account")
@Entity
data class Account (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="account_id")
        val accountId : Int,

        @Column(name="account_numbers", nullable = false, unique = true)
        val accountNumbers : String,

        @Column(nullable = false)
        val ndi : String,

        @Column(name="loan_limit", nullable = false)
        val loanLimit :Int,

        @Column(nullable = false)
        var balance :Int,

        @Column(nullable = false)
        val grade :Int,

        @Column(nullable = false)
        var status : String,

        @Column(name="created_date", nullable = false)
        var createdDate : Timestamp,

){
        @Column(name="loan_start_date")
       var loanStartDate : Timestamp? = null

        fun withdraw(amount : Int, historyTime : Timestamp){
                this.balance += amount

                // 마이너스 통장이 처음 되었다면
                if(this.balance < 0 && this.loanStartDate == null){
                        this.loanStartDate = historyTime
                }
        }
        fun deposit(amount: Int){
                this.balance += amount

                // 더이상 마이너스 통장이 아니라면
                if(this.balance >= 0 && this.loanStartDate != null){
                        this.loanStartDate = null
                }
        }

        fun cancel(){
                this.status = "canceled"
                this.balance = 0
        }
}