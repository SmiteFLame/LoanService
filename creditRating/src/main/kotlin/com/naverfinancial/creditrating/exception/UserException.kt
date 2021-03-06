package com.naverfinancial.creditrating.exception

import org.springframework.http.HttpStatus

abstract class UserException(string: String) : Exception(string) {
    open var status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    class NullNdiException : UserException("NDI 입력값이 없습니다.") {
        override var status: HttpStatus = HttpStatus.BAD_REQUEST
    }

    class NullUserException : UserException("사용자가 존재하지 않습니다") {
        override var status: HttpStatus = HttpStatus.NOT_FOUND
    }

    class FailConnectCBServerException : UserException("CB서버가 열리지 않았습니다.") {
        override var status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    }

    class FailRequestCBServerException : UserException("CB서버 요청에 실패했습니다"){
        override var status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    }
}
