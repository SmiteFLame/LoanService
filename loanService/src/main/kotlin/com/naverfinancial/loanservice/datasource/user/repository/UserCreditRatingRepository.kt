package com.naverfinancial.loanservice.datasource.user.repository

import com.naverfinancial.loanservice.datasource.user.dto.UserCreditRating
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserCreditRatingRepository : JpaRepository<UserCreditRating, Int> {
    fun findUserCreditRatingByNdi(ndi: String): UserCreditRating?
}
