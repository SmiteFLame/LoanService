package com.naverfinancial.creditrating.dto

import java.sql.Timestamp
import javax.persistence.*


@Table(name="credit_rating_search_historys")
@Entity
data class CreditRatingSearchHistory (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private var history_id : Int,

        @Column(nullable = false)
        private var NDI : String,

        @Column(nullable = false)
        private var grade : Int,

        @Column(nullable = false)
        private var created_date : Timestamp
        ){
        fun getHistoryId() : Int{
                return history_id
        }
}