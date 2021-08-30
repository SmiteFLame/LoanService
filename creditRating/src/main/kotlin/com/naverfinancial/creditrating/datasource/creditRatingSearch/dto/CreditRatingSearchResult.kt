package com.naverfinancial.creditrating.datasource.creditRatingSearch.dto

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "credit_rating_search_result")
@Entity
data class CreditRatingSearchResult(
    @Id
    val ndi: String,

    @Column(nullable = false)
    val grade: Int,

    @Column(nullable = false)
    val isPermit: Boolean,

    @Column(name = "history_id", nullable = false)
    val historyId: Int
)
