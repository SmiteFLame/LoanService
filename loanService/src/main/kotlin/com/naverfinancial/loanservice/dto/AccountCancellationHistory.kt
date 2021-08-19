package com.naverfinancial.loanservice.dto

import java.sql.Timestamp
import javax.persistence.*

@Table(name="account_cancellation_historys")
@Entity
data class AccountCancellationHistory (
    @Id
    @Column(name="account_id")
    private var accountId : Int,

    @Column(name="cancellation_date", nullable = false)
    private var cancellationDate : Timestamp
)