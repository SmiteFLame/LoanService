package com.naverfinancial.creditrating.dto

import javax.persistence.*

@Table(name="credit_rating_search_results")
@Entity
data class CreditRatingSearchResult (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var NDI : String,

        @Column(nullable = false)
        var grade : Int,

        @Column(nullable = false)
        var history_id : Int

//        @ManyToOne(fetch = FetchType.LAZY)
//        @JoinColumn(name="history_id", referencedColumnName = "history_id")
//        var creditRatingSearchHistory: CreditRatingSearchHistory
)