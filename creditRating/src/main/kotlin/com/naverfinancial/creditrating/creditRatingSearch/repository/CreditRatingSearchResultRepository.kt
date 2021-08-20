package com.naverfinancial.creditrating.creditRatingSearch.repository

import com.naverfinancial.creditrating.creditRatingSearch.dto.CreditRatingSearchResult
import org.springframework.data.jpa.repository.JpaRepository

interface CreditRatingSearchResultRepository : JpaRepository<CreditRatingSearchResult, String>{
    fun findCreditRatingSearchResultByNDI(NDI : String) : CreditRatingSearchResult
}