package com.naverfinancial.loanservice.exception

import org.springframework.http.HttpStatus

abstract class UserException(string : String) : Exception(string){
    open var status : HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
}

class NullNdiException() : UserException("NDI 입력값이 잘못되었습니다"){
    override var status: HttpStatus = HttpStatus.BAD_REQUEST
}

class NullUserException() : UserException("사용자가 존재하지 않습니다"){
    override var status: HttpStatus = HttpStatus.NOT_FOUND
}

class UnvalidUserException() : UserException("잘못된 회원 정보 입니다"){
    override var status: HttpStatus = HttpStatus.BAD_REQUEST
}