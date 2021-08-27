package com.naverfinancial.loanservice.entity.user.repository

import com.naverfinancial.loanservice.entity.user.dto.UserCreditRating
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserCreditRatingRepository : JpaRepository<UserCreditRating, Integer> {
    fun findUserCreditRatingByNdi(ndi : String) : UserCreditRating?
}
