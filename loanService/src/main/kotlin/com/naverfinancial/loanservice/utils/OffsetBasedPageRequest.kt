package com.naverfinancial.loanservice.utils

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class OffsetBasedPageRequest : Pageable {

    private val limit: Int
    private val offset: Long
    private val sort: Sort

    constructor(limit: Int, offset: Long, sort: Sort) {
        this.limit = limit
        this.offset = offset
        this.sort = sort
    }

    constructor(limit: Int, offset: Long) {
        this.limit = limit
        this.offset = offset
        this.sort = Sort.unsorted()
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
        return if(hasPrevious()){
            OffsetBasedPageRequest(pageSize, offset - pageSize, sort)
        } else{
            this
        }
    }

    override fun first(): Pageable {
        return if(hasPrevious()){
            OffsetBasedPageRequest(pageSize, 0, sort)
        } else{
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