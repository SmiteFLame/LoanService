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
import org.springframework.web.bind.annotation.*
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
        var status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        when (error) {
            is HttpMessageNotReadableException -> status = HttpStatus.BAD_REQUEST
            is HttpTimeoutException -> status = HttpStatus.GATEWAY_TIMEOUT
        }

        return ResponseEntity<String>(error.message, status)
    }

    /**
     * 전체 계좌 정보 리스트조회한다
     *
     * RequestParam : ndi : String
     * Bad Request : NDI가 없는 경우, page, size가 잘못 설정된 경우
     * ResponseEntity : List<Account>
     */
    @GetMapping()
    fun selectAccountList(@RequestParam page: Int?, size: Int?): ResponseEntity<List<Account>> {
        if (page == null || size == null) {
            throw PageableException()
        }
        return ResponseEntity<List<Account>>(accountService.selectAccountList(page, size), HttpStatus.OK)
    }

    /**
     * NDI에 해당되는 계좌 리스트들을 조회한다
     *
     * RequestParam : ndi : String
     * Bad Request : NDI가 없는 경우, page, size가 잘못 설정된 경우
     * ResponseEntity : List<Account>
     */
    @GetMapping("ndi/{ndi}")
    fun selectAccountListByNdi(
        @PathVariable ndi: String,
        @RequestParam page: Int?,
        size: Int?
    ): ResponseEntity<List<Account>> {
        if (page == null || size == null) {
            throw PageableException()
        }
        if (ndi == null||ndi == "") {
            throw NullNdiException()
        }
        if (userService.selectUserByNDI(ndi) == null) {
            throw NullUserException()
        }
        return ResponseEntity<List<Account>>(accountService.selectAccountListByNdi(ndi, page, size), HttpStatus.OK)
    }

    /**
     * 계좌 번호을 입력받아서 계좌 정보를 조회한다
     *
     * PathVariable : ndi : String
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
     * BAD_REQUEST - ndi가 RequestBody에 없을 경우, User에 해당되는 ndi가 없는 경우
     */
    @PostMapping("applyment")
    fun insertAccount(@RequestBody map: Map<String, String>): ResponseEntity<Account> {

        if (!map.containsKey("ndi")) {
            throw NullNdiException()
        }
        if (userService.selectUserByNDI(map.getValue("ndi")) == null) {
            throw NullUserException()
        }

        var account = accountService.selectAccountByNdiStatusNormal(map.getValue("ndi"))
        if (account != null) {
            throw DuplicationAccountException()
        }

        var userCreditRating = userService.selectCreditRating(map.getValue("ndi"))

        if(userCreditRating == null){
            throw NoCreditRating()
        }

        if (!userCreditRating.isPermit) {
            throw BelowCreditRating()
        }

        return ResponseEntity<Account>(
            accountService.openAccount(map.getValue("ndi"), userCreditRating),
            HttpStatus.CREATED
        )
    }

    /**
     * 계좌번호를 입력받아서 대출을 신청하다
     *
     * PathVariable : account-numbers : String
     * RequestBody : type(대출 신청/반환 종류), amount(신청 금액)
     * ResponseEntity : Account
     * BAD_REQUEST - type이 잘못 된 경우, 계좌가 존재하지 않는 경우, 통장이 정지된 경우, 한도보다 더 많은 금액을 대출 신청 한 경우
     */
    @PutMapping("applyment/{account-id}/balance")
    fun updateAccount(
        @PathVariable("account-id") accountId: Int,
        @RequestBody applymentLoanService : ApplymentLoanService
    ): ResponseEntity<Account> {
        if (accountId < 0) {
            throw WrongTypeAccountID()
        }
        if (accountService.selectAccountByAccountId(accountId) == null) {
            throw NullAccountException()
        }
        if (applymentLoanService.amount < 0) {
            throw WrongAmountInput()
        }
        var account = accountService.selectAccountByAccountId(accountId)
        if(account == null){
            throw NullAccountException()
        }

        if(account.status == AccountTypeStatus.CANCELLED){
            throw CancelledAccountException()
        }


        if (applymentLoanService.type == AccountRequestTypeStatus.DEPOSIT) {
            return ResponseEntity<Account>(accountService.depositLoan(account, applymentLoanService.amount), HttpStatus.OK)
        }
        if (applymentLoanService.type == AccountRequestTypeStatus.WITHDRAW) {
            return ResponseEntity<Account>(accountService.withdrawLoan(account, applymentLoanService.amount), HttpStatus.OK)
        } else {
            throw UndefinedTypeException()
        }
    }

    /**
     * 계좌정보를 입력 받아서 계좌를 해지한다.
     *
     * PathVariable : account-numbers : String
     * ResponseEntity : balance : Int, 잔액을 전달
     * BAD_REQUEST - 계좌 정보가 없는 경우
     */
    @DeleteMapping("applyment/{account-id}")
    fun removeAccount(@PathVariable("account-id") accountId: Int): ResponseEntity<Integer> {
        var account = accountService.selectAccountByAccountId(accountId)
        if(account == null){
            throw NullAccountException()
        }

        if(account.status != AccountTypeStatus.CANCELLED){
            throw CancelledAccountException()
        }
        return ResponseEntity<Integer>(accountService.removeAccount(account), HttpStatus.OK)
    }

    /**
     * 전체 계좌 정보 리스트조회한다
     *
     * RequestParam : ndi : String
     * Bad Request : NDI가 없는 경우, page, size가 잘못 설정된 경우
     * ResponseEntity : List<Account>
     */
    @GetMapping("transaction")
    fun selectAccountTransactionList(@RequestParam page: Int?, size: Int?): ResponseEntity<List<AccountTransactionHistory>> {
        if (page == null || size == null) {
            throw PageableException()
        }
        return ResponseEntity<List<AccountTransactionHistory>>(accountService.selectAccountTransactionList(page, size), HttpStatus.OK)
    }

    /**
     * 특정 계좌 아이디의 계좌 거래 정보 리스트조회한다
     *
     * RequestParam : ndi : String
     * Bad Request : NDI가 없는 경우, page, size가 잘못 설정된 경우
     * ResponseEntity : List<Account>
     */
    @GetMapping("transaction/{account-id}")
    fun selectAccountTransactionListByAccountId(@PathVariable("account-id") accountId: Int, @RequestParam page: Int?, size: Int?): ResponseEntity<List<AccountTransactionHistory>> {
        if (page == null || size == null) {
            throw PageableException()
        }
        var account = accountService.selectAccountByAccountId(accountId)
        if(account == null){
            throw NullAccountException()
        }
        return ResponseEntity<List<AccountTransactionHistory>>(accountService.selectAccountTransactionListByAccountId(account, page, size), HttpStatus.OK)
    }
}