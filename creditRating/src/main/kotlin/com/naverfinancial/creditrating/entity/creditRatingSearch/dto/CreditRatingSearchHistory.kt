package com.naverfinancial.creditrating.entity.creditRatingSearch.dto

import java.sql.Timestamp
import javax.persistence.*


@Table(name="credit_rating_search_historys")
@Entity
data class CreditRatingSearchHistory (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="history_id")
        private var historyId : Int,

        @Column(nullable = false)
        private var NDI : String,

        @Column(nullable = false)
        private var grade : Int,

        @Column(name="created_date",nullable = false)
        private var createdDate : Timestamp
        ){
        fun getHistoryId() = historyId
}