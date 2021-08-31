package com.naverfinancial.loanservice.exception

import org.springframework.http.HttpStatus

abstract class CommonException(string: String) : Exception(string) {
    open var status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    class NonIdTypeException() : CommonException("존재하지 않는 IdType입니다"){
        override var status: HttpStatus = HttpStatus.BAD_REQUEST
    }

    class PagingArgumentException : AccountException("잘못된 페이지 조건이 입력되었습니다") {
        override var status: HttpStatus = HttpStatus.BAD_REQUEST
    }
}
