package com.naverfinancial.creditrating.datasource.user.dto

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "user")
@Entity
data class User(
    @Id
    val ndi: String,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(name = "user_name", nullable = false)
    val userName: String,

    @Column(nullable = false)
    val age: Int,

    @Column(nullable = false)
    val salary: Int
)
