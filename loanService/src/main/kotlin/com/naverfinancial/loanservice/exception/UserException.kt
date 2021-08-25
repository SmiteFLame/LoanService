package com.naverfinancial.loanservice.exception

import org.springframework.http.HttpStatus

abstract class UserException(string : String) : Exception(string){
    open var status : HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
}

class NullNdiException() : UserException("NDI가 존재하지 않습니다"){
    override var status: HttpStatus = HttpStatus.BAD_REQUEST
}

class NullUserException() : UserException("User가 존재하지 않습니다"){
    override var status: HttpStatus = HttpStatus.BAD_REQUEST
}