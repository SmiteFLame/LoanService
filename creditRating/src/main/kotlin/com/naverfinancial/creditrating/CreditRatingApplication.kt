package com.naverfinancial.creditrating

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@EnableCaching
@SpringBootApplication
class CreditRatingApplication

fun main(args: Array<String>) {
    runApplication<CreditRatingApplication>(*args)
}
