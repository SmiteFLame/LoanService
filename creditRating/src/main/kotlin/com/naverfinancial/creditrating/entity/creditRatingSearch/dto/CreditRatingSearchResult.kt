package com.naverfinancial.creditrating.entity.creditRatingSearch.dto

import javax.persistence.*

@Table(name="credit_rating_search_result")
@Entity
data class CreditRatingSearchResult (
        @Id
        val ndi : String,

        @Column(nullable = false)
        val grade : Int,

        @Column(name="history_id", nullable = false)
        val historyId : Int
)