package com.naverfinancial.loanservice.wrapper

import java.sql.Timestamp

class LRUCache<K, V>(private val capacity: Int, private val cacheTimeoutLimit: Int) : LinkedHashMap<K, V>() {
    var cacheTimeout = HashMap<K, Timestamp>()

    // Cache 가져오기
    fun getCache(key: K): V? {
        // 캐시가 없으면 null 전송
        if (this[key] == null) {
            return null
        }
        // 현재 캐시가 1초 이상 지난 상태이면 null 전송
        else if (cacheTimeout[key]!!.time + cacheTimeoutLimit < Timestamp(System.currentTimeMillis()).time) {
            return null
        }

        // 현재 cache 가장 앞으로 최신화
        insertCache(key, this[key]!!)
        return this[key]
    }

    // Cache 추가
    fun insertCache(key: K, value: V) {
        // 현재 cache 존재하면 삭제
        if (this[key] != null) {
            this.remove(key)
        }

        // 현재 캐시에 TimeStamp 최신화
        cacheTimeout[key] = Timestamp(System.currentTimeMillis())

        // 캐시 추가
        // LinkedListMap 이용해서 가장 오래된 캐쉬는 자동으로 삭제
        this[key] = value
    }

    // Cache 삭제
    fun removeCache(key: K) {
        this.remove(key)
    }

    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        return this.size > capacity
    }
}
