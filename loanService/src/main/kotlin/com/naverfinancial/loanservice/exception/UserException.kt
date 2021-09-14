package com.naverfinancial.loanservice.exception

import org.springframework.http.HttpStatus

abstract class UserException(string: String) : Exception(string) {
    open var status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    class NullNdiException : UserException("NDI 입력값이 잘못되었습니다") {
        override var status: HttpStatus = HttpStatus.BAD_REQUEST
    }

    class NullUserException : UserException("사용자가 존재하지 않습니다") {
        override var status: HttpStatus = HttpStatus.NOT_FOUND
    }

    class DuplicationEmailException : UserException("이메일 조건이 정확하지 않습니다") {
        override var status: HttpStatus = HttpStatus.BAD_REQUEST
    }

    class InvalidUserException : UserException("입력값이 존재하지 않습니다") {
        override var status: HttpStatus = HttpStatus.BAD_REQUEST
    }

    class InvalidEmailException : UserException("이메일 조건이 정확하지 않습니다") {
        override var status: HttpStatus = HttpStatus.BAD_REQUEST
    }

    class CreditRatingException(message: String, status: HttpStatus) : UserException(message) {
        override var status: HttpStatus = status
    }

    class CreditRatingTimeoutException(
        message: String = "CB서버의 제한시간이 초과되었습니다",
        status: HttpStatus = HttpStatus.GATEWAY_TIMEOUT
    ) : UserException(message) {
        override var status: HttpStatus = status
    }

    class FailConnectCreditRatingServerException : UserException("신용 등급 서버가 열리지 않았습니다") {
        override var status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    }
}
