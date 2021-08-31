package com.naverfinancial.loanservice.exception

import org.springframework.http.HttpStatus

abstract class AccountException(string: String) : Exception(string) {
    open var status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    class DuplicationAccountException : AccountException("이미 계좌를 가지고 있습니다") {
        override var status: HttpStatus = HttpStatus.BAD_REQUEST
    }

    class UndefinedTypeException : AccountException("정의되지 않은 신청 방법입니다") {
        override var status: HttpStatus = HttpStatus.BAD_REQUEST
    }

    class WrongAmountInput : AccountException("잘못된 금액 신청입니다") {
        override var status: HttpStatus = HttpStatus.BAD_REQUEST
    }

    class NullAccountException : AccountException("계좌가 존재하지 않습니다") {
        override var status: HttpStatus = HttpStatus.NOT_FOUND
    }

    class NoCreditRating : AccountException("신용등급이 존재하지 않습니다") {
        override var status: HttpStatus = HttpStatus.NOT_FOUND
    }

    class BelowCreditRating : AccountException("신용등급 미달로 대출 신청이 불가능합니다") {
        override var status: HttpStatus = HttpStatus.OK
    }

    class CancelledAccountException : AccountException("이미 해지된 계좌입니다") {
        override var status: HttpStatus = HttpStatus.OK
    }

    class OverLimitException : AccountException("대출 한도를 초과하였습니다") {
        override var status: HttpStatus = HttpStatus.OK
    }

    class RestLimitException : AccountException("대출 잔고가 남았습니다") {
        override var status: HttpStatus = HttpStatus.OK
    }

    class PagingArgumentException : AccountException("잘못된 페이지 조건이 입력되었습니다") {
        override var status: HttpStatus = HttpStatus.BAD_REQUEST
    }

    class NonIdTypeException() : UserException("존재하지 않는 IdType입니다"){
        override var status: HttpStatus = HttpStatus.BAD_REQUEST
    }
}
