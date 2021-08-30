package com.naverfinancial.creditrating.datasource.creditRatingSearch.repository

import com.naverfinancial.creditrating.datasource.creditRatingSearch.dto.CreditRatingSearchResult
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CreditRatingSearchResultRepository : JpaRepository<CreditRatingSearchResult, String> {
    fun findCreditRatingSearchResultByNdi(ndi: String): CreditRatingSearchResult?
}
