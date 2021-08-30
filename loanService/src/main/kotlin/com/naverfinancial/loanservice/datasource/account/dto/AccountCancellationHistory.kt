package com.naverfinancial.loanservice.datasource.account.dto

import java.sql.Timestamp
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "account_cancellation_history")
@Entity
data class AccountCancellationHistory(
    @Id
    @Column(name = "account_id")
    val accountId: Int,

    @Column(name = "cancellation_date", nullable = false)
    val cancellationDate: Timestamp
)
