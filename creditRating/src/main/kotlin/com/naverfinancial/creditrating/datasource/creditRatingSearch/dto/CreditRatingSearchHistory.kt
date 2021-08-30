package com.naverfinancial.creditrating.datasource.creditRatingSearch.dto

import java.sql.Timestamp
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "credit_rating_search_history")
@Entity
data class CreditRatingSearchHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    val historyId: Int,

    @Column(nullable = false)
    val ndi: String,

    @Column(nullable = false)
    val grade: Int,

    @Column(name = "created_date", nullable = false)
    val createdDate: Timestamp
)
