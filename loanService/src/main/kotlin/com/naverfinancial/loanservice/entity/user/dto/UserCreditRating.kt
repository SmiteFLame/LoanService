package com.naverfinancial.loanservice.entity.user.dto

import java.sql.Timestamp
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name="user_credit_rating")
@Entity
data class UserCreditRating (
    @Id
    var ndi : String?,

    @Column(nullable = false)
    val grade : Int,

    @Column(nullable = false)
    val isPermit : Boolean,

    @Column(name = "created_date", nullable = false)
    val createdDate : Timestamp,
)
