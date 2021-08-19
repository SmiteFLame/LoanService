package com.naverfinancial.loanservice.dto

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

    @Column(nullable = false)
    private val user_name : String,

    @Column(nullable = false)
    private val age : Int,

    @Column(nullable = false)
    private val salary : Int
){

    fun getNDI() : String{
        return NDI
    }
}