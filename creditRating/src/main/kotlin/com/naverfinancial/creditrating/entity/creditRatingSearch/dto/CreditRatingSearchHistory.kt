package com.naverfinancial.creditrating.entity.creditRatingSearch.dto

import java.sql.Timestamp
import javax.persistence.*


@Table(name="credit_rating_search_historys")
@Entity
data class CreditRatingSearchHistory (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="history_id")
        val historyId : Int,

        @Column(nullable = false)
        val NDI : String,

        @Column(nullable = false)
        val grade : Int,

        @Column(name="created_date",nullable = false)
        val createdDate : Timestamp
        )