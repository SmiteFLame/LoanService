package com.naverfinancial.loanservice.cache

import com.naverfinancial.loanservice.datasource.user.dto.UserCreditRating
import com.naverfinancial.loanservice.wrapper.LRUCache
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.timer

object UserCreditRatingCache {
    private val cache = LRUCache<String, UserCreditRating>(3)
    private val cacheVersion = HashMap<String, UUID>()

    fun getCache(ndi: String): UserCreditRating? {
        return cache[ndi]
    }

    fun insertCache(ndi: String, userCreditRating: UserCreditRating) {
        if (cache[ndi] != null) {
            cache.remove(ndi)
        }
        cacheVersion[ndi] = UUID.randomUUID()
        cache[ndi] = userCreditRating

        Thread {
            val uuid = cacheVersion[ndi]
            Thread.sleep(10000)
            if(cacheVersion[ndi] == uuid){
                cache.remove(ndi)
                cacheVersion.remove(ndi)
            }
        }.start()
    }

    fun removeCache(ndi: String) {
        cache.remove(ndi)
    }
}
