package com.naverfinancial.loanservice.utils

import java.util.regex.Pattern

class EmailValiation {
    companion object {
        fun checkEmailValid(email: String): Boolean {
            val regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"
            val pattern = Pattern.compile(regex)
            return pattern.matcher(email).matches()
        }
    }
}
