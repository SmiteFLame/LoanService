package com.naverfinancial.loanservice.utils

import kotlin.random.Random

class AccountNumberGenerators {

    companion object {
        fun generatorAccountNumbers() : String{
            // 000 – 0000 – 0000 - 0
            var accountNumbers : String = ""
            for(i in 0..14){
                if(i == 3 || i == 8 || i ==13){
                    accountNumbers += "-"
                } else{
                    accountNumbers += Random.nextInt(48,58).toChar()
                }
            }
            return accountNumbers
        }
    }
}