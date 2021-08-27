package com.naverfinancial.creditrating.datasource.creditRatingSearch.repository

import com.naverfinancial.creditrating.datasource.creditRatingSearch.dto.CreditRatingSearchHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CreditRatingSearchHistoryRepository : JpaRepository<CreditRatingSearchHistory, Integer> {
}
