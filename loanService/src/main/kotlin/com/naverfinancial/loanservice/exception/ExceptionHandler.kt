package com.naverfinancial.loanservice.exception

import org.springframework.dao.CannotAcquireLockException
import org.springframework.data.mapping.PropertyReferenceException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.transaction.CannotCreateTransactionException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.server.MethodNotAllowedException
import java.lang.IllegalStateException

@RestControllerAdvice
class ExceptionHandler {
    @ExceptionHandler
    fun userExceptionHandler(error: UserException): ResponseEntity<String> {
        println(error.message)
        return ResponseEntity<String>(error.message, error.status)
    }

    @ExceptionHandler
    fun exceptionHandler(error: Exception): ResponseEntity<String> {
        var message = error.message
        var status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        when (error) {
            is HttpMessageNotReadableException -> {
                message = "입력값이 잘못 들어왔습니다"
                status = HttpStatus.BAD_REQUEST
            }
            is MethodNotAllowedException -> {
                message = "없는 URL입니다"
                status = HttpStatus.BAD_REQUEST
            }
            // RequestParam이 존재하지 않는 경우
            is MissingServletRequestParameterException -> {
                message = "필요한 파라미터 조건이 없습니다"
                status = HttpStatus.BAD_REQUEST
            }
            // RequestParam 일부 입력값만 입력이 되지 않은 경우
            is IllegalStateException -> {
                message = "필요한 파라미터 조건이 없습니다"
                status = HttpStatus.BAD_REQUEST
            }
            // RequestParam 타입이 잘못 들어온 경우
            is MethodArgumentTypeMismatchException -> {
                message = "파라미터 입력값이 잘못되었습니다"
                status = HttpStatus.BAD_REQUEST
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
            is PropertyReferenceException -> {
                message = "정렬할 수 없는 속성 값입니다"
                status = HttpStatus.BAD_REQUEST
            }
        }

        println(message)
        return ResponseEntity<String>(message, status)
    }
}
