package com.naverfinancial.creditrating.repository

import com.naverfinancial.creditrating.dto.CreditRatingSearchResult
import org.springframework.data.jpa.repository.JpaRepository

interface CreditRatingSearchResultRepository : JpaRepository<CreditRatingSearchResult, String>{
}