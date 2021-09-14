package com.naverfinancial.loanservice.cache

import com.naverfinancial.loanservice.datasource.user.dto.UserCreditRating
import com.naverfinancial.loanservice.wrapper.LRUCache
import java.util.*
import kotlin.collections.LinkedHashMap

object UserCreditRatingCache {
     private val cache = LRUCache<String, UserCreditRating>(3)

     fun getCache(ndi: String) : UserCreditRating?{
          return cache[ndi]
     }

     fun insertCache(ndi : String, userCreditRating: UserCreditRating){
          if(cache[ndi] != null){
               cache.remove(ndi)
          }
          cache[ndi] = userCreditRating
     }

     fun removeCache(ndi : String){
          cache.remove(ndi)
     }
}
