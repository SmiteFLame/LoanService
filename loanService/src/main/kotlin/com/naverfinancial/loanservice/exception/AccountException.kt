package com.naverfinancial.loanservice.exception

import org.springframework.http.HttpStatus

class DuplicationAccountException : Exception("이미 계좌를 가지고 있습니다")

class UndefinedTypeException : Exception("정의되지 않은 신청 방법입니다")

class WrongTypeAccountID : Exception("잘못된 계좌 아이디 입니다")

class NullAccountException : Exception("계좌가 존재하지 않습니다")

class BelowCreditRating : Exception("신용등급이 미달 되었습니다.")