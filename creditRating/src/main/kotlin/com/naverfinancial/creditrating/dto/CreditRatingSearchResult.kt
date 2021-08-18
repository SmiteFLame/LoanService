package com.naverfinancial.creditrating.dto

import javax.persistence.*

//@Table(name="credit_rating_search_results")
//@Entity
data class CreditRatingSearchResult (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var NDI : String,

        @Column(nullable = false)
        var grade : Int,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name="history_id")
        var history_id: CreditRatingSearchHistory
)