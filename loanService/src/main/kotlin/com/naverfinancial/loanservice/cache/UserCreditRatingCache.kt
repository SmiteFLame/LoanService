package com.naverfinancial.loanservice.cache

import com.naverfinancial.loanservice.datasource.user.dto.UserCreditRating

object UserCreditRatingCache {
    private var cache = HashMap<String, UserCreditRating>()

    /**
     * 캐시 구현을 해야되는 기능
     * 1. 캐시 추가
     * 2. 캐시 타임 아웃 삭제
     * 3. 데이터 변경시 삭제
     *
     * 수정 완료후
     * Cache 위치 수정(Service가 아니라 Controller에 붙이기)
     *
     */
}
