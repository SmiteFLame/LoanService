package com.naverfinancial.loanservice.cache

import com.naverfinancial.loanservice.datasource.user.dto.UserCreditRating
import com.naverfinancial.loanservice.wrapper.LRUCache
import java.sql.Timestamp

object UserCreditRatingCache {
    private val cache = LRUCache<String, UserCreditRating>(3)
    private const val cacheTime: Int = 1000

    // Cache 가져오기
    fun getCache(ndi: String): UserCreditRating? {
        // 캐시가 없으면 null 전송
        if (cache[ndi] == null) {
            return null
        }
        // 현재 캐시가 1초 이상 지난 상태이면 null 전송
        else if (cache[ndi]!!.cacheTimestamp!!.time + cacheTime < Timestamp(System.currentTimeMillis()).time) {
            return null
        }

        // 현재 cache 가장 앞으로 최신화
        insertCache(ndi, cache[ndi]!!)
        return cache[ndi]
    }

    // Cache 추가
    fun insertCache(ndi: String, userCreditRating: UserCreditRating) {
        // 현재 cache 존재하면 삭제
        if (cache[ndi] != null) {
            cache.remove(ndi)
        }

        // 현재 캐시에 TimeStamp 최신화
        userCreditRating.cacheTimestamp = Timestamp(System.currentTimeMillis())

        // 캐시 추가
        // LinkedListMap 이용해서 가장 오래된 캐쉬는 자동으로 삭제
        cache[ndi] = userCreditRating
    }

    // Cache 삭제
    fun removeCache(ndi: String) {
        cache.remove(ndi)
    }
}
