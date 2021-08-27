package com.naverfinancial.loanservice.controller

import com.naverfinancial.loanservice.entity.account.dto.Account
import com.naverfinancial.loanservice.entity.account.dto.AccountTransactionHistory
import com.naverfinancial.loanservice.enumclass.AccountRequestTypeStatus
import com.naverfinancial.loanservice.enumclass.AccountTypeStatus
import com.naverfinancial.loanservice.exception.*
import com.naverfinancial.loanservice.wrapper.ApplymentLoanService
import com.naverfinancial.loanservice.service.AccountService
import com.naverfinancial.loanservice.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.*
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.server.MethodNotAllowedException
import java.net.http.HttpTimeoutException

@RestController
@RequestMapping("/accounts")
class AccountController {

    @Autowired
    lateinit var accountService: AccountService

    @Autowired
    lateinit var userService: UserService

    @ExceptionHandler(UserException::class)
    fun userExceptionHandler(error: UserException): ResponseEntity<String> {
        return ResponseEntity<String>(error.message, error.status)
    }

    @ExceptionHandler(AccountException::class)
    fun accountExceptionHandler(error: AccountException): ResponseEntity<String> {
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
            is MethodNotAllowedException ->{
                message = "없는 URL입니다"
                status = HttpStatus.BAD_REQUEST
            }
            is MissingServletRequestParameterException ->{
                message = "URL Query문이 존재하지 않거나 잘못되었습니다"
                status = HttpStatus.BAD_REQUEST
            }
            is MethodArgumentTypeMismatchException ->{
                message = "URL이 Param 입력값이 잘못 들어왔습니다"
                status = HttpStatus.BAD_REQUEST
            }
        }

        return ResponseEntity<String>(message, status)
    }

    /**
     * 전체 계좌 정보 리스트조회한다
     *
     * RequestParam : ndi : String
     * Bad Request : NDI가 없는 경우, limit, offset가 잘못 설정된 경우
     * ResponseEntity : List<Account>
     */
    @GetMapping()
    fun selectAccountList(@RequestParam limit: Int, offset: Int): ResponseEntity<List<Account>> {
        return ResponseEntity<List<Account>>(accountService.selectAccountList(limit, offset), HttpStatus.OK)
    }

    /**
     * NDI에 해당되는 계좌 리스트들을 조회한다
     *
     * RequestParam : ndi : String
     * Bad Request : NDI가 없는 경우, limit, offset가 잘못 설정된 경우
     * ResponseEntity : List<Account>
     */
    @GetMapping("ndi/{ndi}")
    fun selectAccountListByNdi(
        @PathVariable ndi: String, @RequestParam limit: Int, offset: Int
    ): ResponseEntity<List<Account>> {
        if (ndi == null || ndi == "") {
            throw NullNdiException()
        }
        if (userService.selectUserByNDI(ndi) == null) {
            throw NullUserException(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity<List<Account>>(accountService.selectAccountListByNdi(ndi, limit, offset), HttpStatus.OK)
    }

    /**
     * 계좌 아이디를 입력받아서 계좌 정보를 조회한다
     *
     * PathVariable : account_id : Int
     * ResponseEntity : Account
     * GATEWAY_TIMEOUT - 10초 이내로 데이터 요청을 신용등급을 못 가져온 경우
     */
    @GetMapping("{account-id}")
    fun selectAccountByAccountId(@PathVariable("account-id") accountId: Int): ResponseEntity<Account> {
        return ResponseEntity<Account>(accountService.selectAccountByAccountId(accountId), HttpStatus.OK)
    }

    /**
     * NDI을 입력받아서 마이너스 계좌를 개설한다, 이미 계좌가 존재하는 경우 그 계좌를 반환한다
     *
     * RequestBody : ndi : String
     * ResponseEntity : Account
     * BAD_REQUEST - ndi가 잘못 들어올 경우, 이미 계좌를 가지고 있을 경우
     * NOT_FOUND - 존재하지 않는 유저일 경우, 신용등급이 없는 경우
     * OK - 신용 등급이 미달인 경우
     * CREATED - 성공
     */
    @PostMapping("applyment")
    fun insertAccount(@RequestBody map: Map<String, String>): ResponseEntity<Account> {

        if (!map.containsKey("ndi")) {
            throw NullNdiException()
        }
        if (userService.selectUserByNDI(map.getValue("ndi")) == null) {
            throw NullUserException(HttpStatus.NOT_FOUND)
        }

        var account = accountService.selectAccountByNdiStatusNormal(map.getValue("ndi"))
        if (account != null) {
            throw DuplicationAccountException()
        }

        var userCreditRating = userService.selectCreditRating(map.getValue("ndi")) ?: throw NoCreditRating()

        if (!userCreditRating.isPermit) {
            throw BelowCreditRating()
        }

        return ResponseEntity<Account>(
            accountService.openAccount(map.getValue("ndi"), userCreditRating),
            HttpStatus.CREATED
        )
    }

    /**
     * 계좌 아이디를 입력받아서 대출을 신청하다
     *
     * PathVariable : account-id : Int
     * RequestBody : type(대출 신청/반환 종류), amount(신청 금액)
     * ResponseEntity : Account
     * BAD_REQUEST - type이 잘못 된 경우, 통장이 정지된 경우, 잘못된 금액이 들어온 경우, 이미 해지된 계좌가 들어온 경우
     * NOT_FOUND -  계좌가 존재하지 않는 경우
     * OK - 한도보다 더 많은 금액을 대출 신청 한 경우
     * CREATED - 성공
     */
    @PutMapping("applyment/{account-id}/balance")
    fun updateAccount(
        @PathVariable("account-id") accountId: Int,
        @RequestBody applymentLoanService: ApplymentLoanService
    ): ResponseEntity<Account> {
        if (accountId < 0) {
            throw WrongTypeAccountID()
        }
        if (applymentLoanService.amount < 0) {
            throw WrongAmountInput()
        }
        var account = accountService.selectAccountByAccountId(accountId) ?: throw NullAccountException()

        if (account.status == AccountTypeStatus.CANCELLED) {
            throw CancelledAccountException()
        }


        if (applymentLoanService.type == AccountRequestTypeStatus.DEPOSIT) {
            return ResponseEntity<Account>(
                accountService.depositLoan(account, applymentLoanService.amount),
                HttpStatus.CREATED
            )
        }
        if (applymentLoanService.type == AccountRequestTypeStatus.WITHDRAW) {
            return ResponseEntity<Account>(
                accountService.withdrawLoan(account, applymentLoanService.amount),
                HttpStatus.CREATED
            )
        } else {
            throw UndefinedTypeException()
        }
    }

    /**
     * 계좌 아이디를 입력 받아서 계좌를 해지한다.
     *
     * PathVariable : account-id : Int
     * ResponseEntity : balance : Int, 잔액을 전달
     * BAD_REQUEST - 계좌 정보가 없는 경우
     */
    @DeleteMapping("applyment/{account-id}")
    fun removeAccount(@PathVariable("account-id") accountId: Int): ResponseEntity<Integer> {
        var account = accountService.selectAccountByAccountId(accountId) ?: throw NullAccountException()

        if (account.status == AccountTypeStatus.CANCELLED) {
            throw CancelledAccountException()
        }
        return ResponseEntity<Integer>(accountService.removeAccount(account), HttpStatus.CREATED)
    }

    /**
     * 전체 계좌 정보 리스트조회한다
     *
     * RequestParam : ndi : String
     * Bad Request : NDI가 없는 경우, limit, offset가 잘못 설정된 경우
     * ResponseEntity : List<Account>
     */
    @GetMapping("transaction")
    fun selectAccountTransactionList(
        @RequestParam limit: Int,
        offset: Int
    ): ResponseEntity<List<AccountTransactionHistory>> {
        return ResponseEntity<List<AccountTransactionHistory>>(
            accountService.selectAccountTransactionList(limit, offset),
            HttpStatus.OK
        )
    }

    /**
     * 특정 계좌 아이디의 계좌 거래 정보 리스트조회한다
     *
     * RequestParam : ndi : String
     * Bad Request : NDI가 없는 경우, limit, offset가 잘못 설정된 경우
     * ResponseEntity : List<Account>
     */
    @GetMapping("transaction/{account-id}")
    fun selectAccountTransactionListByAccountId(
        @PathVariable("account-id") accountId: Int,
        @RequestParam limit: Int,
        offset: Int
    ): ResponseEntity<List<AccountTransactionHistory>> {
        var account = accountService.selectAccountByAccountId(accountId)
        if (account == null) {
            throw NullAccountException()
        }
        return ResponseEntity<List<AccountTransactionHistory>>(
            accountService.selectAccountTransactionListByAccountId(
                account,
                limit,
                offset
            ), HttpStatus.OK
        )
    }
}
