package com.naverfinancial.creditrating.dto

import java.sql.Timestamp
import javax.persistence.*


@Table(name="credit_rating_search_historys")
@Entity
data class CreditRatingSearchHistory (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var history_id : Int,

        @Column(nullable = false)
        var NDI : String,

        @Column(nullable = false)
        var grade : Int,

        @Column(nullable = false)
        var created_date : Timestamp
        )