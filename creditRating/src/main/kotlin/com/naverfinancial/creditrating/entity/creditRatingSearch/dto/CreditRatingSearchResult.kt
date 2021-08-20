package com.naverfinancial.creditrating.entity.creditRatingSearch.dto

import javax.persistence.*

@Table(name="credit_rating_search_results")
@Entity
data class CreditRatingSearchResult (
        @Id
        val NDI : String,

        @Column(nullable = false)
        val grade : Int,

        @Column(name="history_id", nullable = false)
        val historyId : Int
)