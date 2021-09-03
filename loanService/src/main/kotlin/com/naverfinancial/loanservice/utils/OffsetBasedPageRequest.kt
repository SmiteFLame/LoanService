package com.naverfinancial.loanservice.utils

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class OffsetBasedPageRequest : Pageable {

    private val MAX = 10
    private var limit: Int
    private var offset: Long
    private val sort: Sort

    constructor(limit: Int, offset: Long, sort: Sort) {
        this.limit = limit
        this.offset = offset
        this.sort = sort
        checkValid()
    }

    private fun checkValid() {
        if (limit > MAX) {
            this.limit = MAX
        } else if (limit <= 0) {
            this.limit = 1
        }
        if (offset < 0) {
            this.offset = 0
        }
    }

    override fun getPageNumber(): Int {
        return (offset / limit).toInt()
    }

    override fun getPageSize(): Int {
        return limit
    }

    override fun getOffset(): Long {
        return offset
    }

    override fun getSort(): Sort {
        return sort
    }

    override fun next(): Pageable {
        return OffsetBasedPageRequest(pageSize, offset + pageSize, sort)
    }

    override fun previousOrFirst(): Pageable {
        return if (hasPrevious()) {
            OffsetBasedPageRequest(pageSize, offset - pageSize, sort)
        } else {
            this
        }
    }

    override fun first(): Pageable {
        return if (hasPrevious()) {
            OffsetBasedPageRequest(pageSize, 0, sort)
        } else {
            this
        }
    }

    override fun withPage(pageNumber: Int): Pageable {
        TODO("Not yet implemented")
    }

    override fun hasPrevious(): Boolean {
        return offset > limit
    }
}
