package com.naverfinancial.loanservice.exception

import org.springframework.http.HttpStatus

abstract class UserException(string : String) : Exception(string){
    open var status : HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
}

class NullNdiException() : UserException("NDI 입력값이 잘못되었습니다"){
    override var status: HttpStatus = HttpStatus.BAD_REQUEST
}

class NullUserException(status : HttpStatus) : UserException("사용자가 존재하지 않습니다"){
    override var status: HttpStatus = status
}

class DuplicationEmailException() : UserException("이미 이메일을에 해당되는 유저가 존재합니다"){
    override  var status : HttpStatus = HttpStatus.BAD_REQUEST
}

class UnvalidUserException() : UserException("잘못된 회원 정보 입니다"){
    override var status: HttpStatus = HttpStatus.BAD_REQUEST
}