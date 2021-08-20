package com.naverfinancial.creditrating

import com.naverfinancial.creditrating.entity.user.config.UserConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CreditRatingApplication

fun main(args: Array<String>) {
    runApplication<CreditRatingApplication>(*args)
}
