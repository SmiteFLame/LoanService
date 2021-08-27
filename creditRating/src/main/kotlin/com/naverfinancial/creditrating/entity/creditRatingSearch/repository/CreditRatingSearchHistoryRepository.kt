package com.naverfinancial.creditrating.entity.creditRatingSearch.repository

import com.naverfinancial.creditrating.entity.creditRatingSearch.dto.CreditRatingSearchHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CreditRatingSearchHistoryRepository : JpaRepository<CreditRatingSearchHistory, Integer> {
}
