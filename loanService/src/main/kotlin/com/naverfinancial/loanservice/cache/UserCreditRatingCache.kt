package com.naverfinancial.loanservice.cache

import com.naverfinancial.loanservice.datasource.user.dto.UserCreditRating
import com.naverfinancial.loanservice.wrapper.LRUCache
import java.sql.Timestamp

object UserCreditRatingCache {
    private val cache = LRUCache<String, UserCreditRating>(3)
    private const val cacheTime: Int = 1000

    // Cache 가져오기
    fun getCache(ndi: String): UserCreditRating? {
        if (cache[ndi] == null) {
            return null
        } else if (cache[ndi]!!.cacheTimestamp!!.time + cacheTime <= Timestamp(System.currentTimeMillis()).time) {
            return null
        }
        updateCache(ndi)
        return cache[ndi]
    }

    // Cache 추가
    fun insertCache(ndi: String, userCreditRating: UserCreditRating) {
        if (cache[ndi] != null) {
            cache.remove(ndi)
        }
        cache[ndi] = userCreditRating
        updateCache(ndi)
    }

    // Cache 시간 변경
    private fun updateCache(ndi : String){
        cache[ndi]!!.cacheTimestamp = Timestamp(System.currentTimeMillis())
    }

    // Cache 삭제
    fun removeCache(ndi: String) {
        cache.remove(ndi)
    }
}
