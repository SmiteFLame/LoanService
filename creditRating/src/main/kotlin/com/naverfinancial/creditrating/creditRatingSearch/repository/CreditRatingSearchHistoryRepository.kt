package com.naverfinancial.creditrating.creditRatingSearch.repository

import com.naverfinancial.creditrating.creditRatingSearch.dto.CreditRatingSearchHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CreditRatingSearchHistoryRepository : JpaRepository<CreditRatingSearchHistory, Integer> {
}