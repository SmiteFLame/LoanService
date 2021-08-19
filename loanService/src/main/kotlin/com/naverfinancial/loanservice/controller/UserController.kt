package com.naverfinancial.loanservice.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController{
    @GetMapping("{emails}/emails")
    fun searchEmail(@PathVariable emails : String){

    }
}