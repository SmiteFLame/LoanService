package com.naverfinancial.creditrating.repository

import com.naverfinancial.creditrating.dto.CreditRatingSearchHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CreditRatingSearchHistoryRepository : JpaRepository<CreditRatingSearchHistory, Integer> {
}