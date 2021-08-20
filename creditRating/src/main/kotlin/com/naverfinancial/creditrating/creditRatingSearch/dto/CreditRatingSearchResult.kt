package com.naverfinancial.creditrating.creditRatingSearch.dto

import javax.persistence.*

@Table(name="credit_rating_search_results")
@Entity
data class CreditRatingSearchResult (
        @Id
        private var NDI : String,

        @Column(nullable = false)
        private var grade : Int,

        @Column(name="history_id", nullable = false)
        private var historyId : Int

//        @ManyToOne(fetch = FetchType.LAZY)
//        @JoinColumn(name="history_id", referencedColumnName = "history_id")
//        var creditRatingSearchHistory: CreditRatingSearchHistory
){

}