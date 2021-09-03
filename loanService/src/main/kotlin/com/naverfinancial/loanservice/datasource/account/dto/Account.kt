package com.naverfinancial.loanservice.datasource.account.dto

import com.naverfinancial.loanservice.enumclass.AccountTypeStatus
import java.sql.Timestamp
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "account")
@Entity
data class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    val accountId: Int,

    @Column(name = "account_number", nullable = false, unique = true)
    val accountNumber: String,

    @Column(nullable = false)
    val ndi: String,

    @Column(name = "loan_limit", nullable = false)
    val loanLimit: Int,

    @Column(nullable = false)
    var balance: Int,

    @Column(nullable = false)
    val grade: Int,

    @Column(nullable = false)
    var status: AccountTypeStatus,

    @Column(name = "created_date", nullable = false)
    var createdDate: Timestamp,
) {
    @Column(name = "cancelled_date")
    var cancelledDate: Timestamp? = null

    companion object {
        fun getPrimaryKey() = "accountId"
    }

    fun withdraw(amount: Int) {
        this.balance -= amount
    }

    fun deposit(amount: Int) {
        this.balance += amount
    }

    fun cancel(historyTime: Timestamp) {
        this.status = AccountTypeStatus.CANCELLED
        this.cancelledDate = historyTime
        this.balance = 0
    }
}
