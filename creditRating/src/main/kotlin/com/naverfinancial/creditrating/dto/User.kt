package com.naverfinancial.creditrating.dto

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table


@Table(name="users")
@Entity
data class User (
        @Id
        val NDI : String,

        @Column(unique = true, nullable = false)
        val email : String,

        @Column(nullable = false)
        val user_name : String,

        @Column(nullable = false)
        val age : Int,

        @Column(nullable = false)
        val salary : Int
        )