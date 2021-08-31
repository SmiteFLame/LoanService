package com.naverfinancial.loanservice.utils

import com.naverfinancial.loanservice.exception.AccountException

class PagingUtil {
    companion object {
        fun getPage(limit: Int, offset: Int): Int {
            return offset / limit
        }

        fun checkIsValid(limit: Int, offset: Int) : Boolean{
            if (limit <= 0 || offset < 0) {
                return false
            }
            return true
        }
    }
}
