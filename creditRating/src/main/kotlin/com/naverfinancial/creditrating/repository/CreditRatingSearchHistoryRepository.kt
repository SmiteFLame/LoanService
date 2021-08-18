package com.naverfinancial.creditrating.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CreditRatingSearchHistoryRepository : JpaRepository<CreditRatingSearchHistoryRepository, Integer> {
}