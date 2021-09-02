package com.naverfinancial.creditrating.datasource.creditRatingSearch.repository

import com.naverfinancial.creditrating.datasource.creditRatingSearch.dto.CreditRatingSearchResult
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.stereotype.Repository
import javax.persistence.LockModeType

@Repository
interface CreditRatingSearchResultRepository : JpaRepository<CreditRatingSearchResult, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findCreditRatingSearchResultByNdi(ndi: String): CreditRatingSearchResult?
}
