package com.naverfinancial.loanservice.wrapper

class LRUCache<K, V>(private val capacity: Int) : LinkedHashMap<K, V>() {

    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        return this.size > capacity
    }
}
