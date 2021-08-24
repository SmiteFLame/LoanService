package com.naverfinancial.creditrating.entity.creditRatingSearch.repository

import com.naverfinancial.creditrating.entity.creditRatingSearch.dto.CreditRatingSearchResult
import org.springframework.data.jpa.repository.JpaRepository

interface CreditRatingSearchResultRepository : JpaRepository<CreditRatingSearchResult, String>{
    fun findCreditRatingSearchResultByNdi(ndi : String) : CreditRatingSearchResult?
}