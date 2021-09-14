package com.naverfinancial.loanservice.wrapper

class LRUCache<K, V>(private val capacity: Int) : LinkedHashMap<K, V>() {

    // 기본 값은 false, 재정의를 해야만 가장 오래된 요소를 삭제한다
    // map의 사이즈가 capacity이 넘으면 가장 오래된 데이터를 삭제
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        return this.size > capacity
    }
}
