package com.naverfinancial.loanservice.utils

import com.naverfinancial.loanservice.exception.PagingArgumentExcetpion

class PagingUtil {
    companion object {
        fun getPage(limit: Int, offset: Int): Int{
            if(limit < 0 || offset < 0){
                throw PagingArgumentExcetpion()
            }
            return offset / limit
        }
    }
}