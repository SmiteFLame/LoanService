package com.naverfinancial.creditrating.user.dto

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table


@Table(name="users")
@Entity
data class User (
        @Id
        private val NDI : String,

        @Column(unique = true, nullable = false)
        private val email : String,

        @Column(name = "user_name", nullable = false)
        private val userName : String,

        @Column(nullable = false)
        private val age : Int,

        @Column(nullable = false)
        private val salary : Int
        ){

        fun getNDI() = NDI
        fun getAge() = age
        fun getSalary() = salary
}