package com.naverfinancial.loanservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@EnableCaching
@SpringBootApplication
class LoanServiceApplication

fun main(args: Array<String>) {
    runApplication<LoanServiceApplication>(*args)
}
