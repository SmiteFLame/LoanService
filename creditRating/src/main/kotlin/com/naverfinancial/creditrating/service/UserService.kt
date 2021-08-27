package com.naverfinancial.creditrating.service

import com.naverfinancial.creditrating.datasource.user.dto.User

interface UserService {
    fun selectUserByNDI(ndi : String) : User?
}
