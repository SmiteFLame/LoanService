package com.naverfinancial.creditrating.exception

import org.springframework.dao.CannotAcquireLockException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.transaction.CannotCreateTransactionException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.http.HttpTimeoutException

@RestControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(UserException::class)
    fun userExceptionHandler(error: UserException): ResponseEntity<String> {
        return ResponseEntity<String>(error.message, error.status)
    }

    @ExceptionHandler
    fun exceptionHandler(error: Exception): ResponseEntity<String> {
        var message = error.message
        var status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        when (error) {
            is HttpMessageNotReadableException -> {
                message = "일부 입력값이 없습니다"
                status = HttpStatus.BAD_REQUEST
            }
            is HttpTimeoutException -> {
                message = "CB서버의 제한시간이 초과되었습니다"
                status = HttpStatus.GATEWAY_TIMEOUT
            }
            // 데이터베이스에서 여러번 동시에 접속 하는 경우
            is CannotAcquireLockException -> {
                message = "다른 데이터 곳에서 중복으로 사용 중입니다"
                status = HttpStatus.INTERNAL_SERVER_ERROR
            }
            // 데이터베이스에 접속할 수 없는 경우
            is CannotCreateTransactionException -> {
                message = "데이터베이스에 접속할 수 없습니다"
                status = HttpStatus.INTERNAL_SERVER_ERROR
            }
        }
        println(message)
        return ResponseEntity<String>(message, status)
    }
}
