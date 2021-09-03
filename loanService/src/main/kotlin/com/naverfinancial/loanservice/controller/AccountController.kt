package com.naverfinancial.loanservice.controller

import com.naverfinancial.loanservice.datasource.account.dto.Account
import com.naverfinancial.loanservice.datasource.account.dto.AccountTransactionHistory
import com.naverfinancial.loanservice.datasource.account.repository.AccountRepository
import com.naverfinancial.loanservice.datasource.account.repository.AccountTransactionHistoryRepository
import com.naverfinancial.loanservice.datasource.user.repository.UserCreditRatingRepository
import com.naverfinancial.loanservice.datasource.user.repository.UserRepository
import com.naverfinancial.loanservice.enumclass.AccountRequestTypeStatus
import com.naverfinancial.loanservice.enumclass.AccountTypeStatus
import com.naverfinancial.loanservice.exception.AccountException
import com.naverfinancial.loanservice.exception.UserException
import com.naverfinancial.loanservice.service.AccountService
import com.naverfinancial.loanservice.utils.OffsetBasedPageRequest
import com.naverfinancial.loanservice.wrapper.ApplymentLoanService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/accounts")
class AccountController {

    @Autowired
    lateinit var accountService: AccountService

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var accountTransactionHistoryRepository: AccountTransactionHistoryRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userCreditRatingRepository: UserCreditRatingRepository

    /**
     * 전체(NDI별) 계좌 정보 리스트조회한다
     *
     * RequestParam : ndi : String
     * ResponseEntity : Page<Account>
     * BAD_REQUEST : NDI가 없는 경우, limit, offset가 잘못 설정된 경우
     * NOT_FOUND : Account가 없는 경우
     */
    @GetMapping
    fun selectAccountList(
        ndi: String?,
        @RequestParam(defaultValue = "NORMAL") status: AccountTypeStatus,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Long,
        @RequestParam(value = "sort-by") sortBy: String?,
        @RequestParam(value = "order-by") orderBy: String?,
    ): ResponseEntity<Page<Account>> {
        val accounts = accountService.selectAccounts(
            ndi,
            status,
            OffsetBasedPageRequest(limit, offset, sortBy, orderBy, Account.getPrimaryKey())
        )

        if (!accounts.hasContent()) {
            throw AccountException.NullAccountException()
        }

        return ResponseEntity<Page<Account>>(accounts, HttpStatus.OK)
    }

    /**
     * 계좌 아이디를 입력받아서 계좌 정보를 조회한다
     *
     * PathVariable : account_id : Int
     * ResponseEntity : Account
     * NOT_FOUND : 게좌 정보가 존재하지 않는 경우
     */
    @GetMapping("{account-id}")
    fun selectAccountByAccountId(@PathVariable("account-id") accountId: Int): ResponseEntity<Account> {
        return ResponseEntity<Account>(accountService.selectAccountByAccountID(accountId), HttpStatus.OK)
    }

    /**
     * NDI을 입력받아서 마이너스 계좌를 개설한다
     *
     * RequestBody : ndi : String
     * ResponseEntity : Account
     * BAD_REQUEST - ndi가 잘못 들어올 경우, 이미 계좌를 가지고 있을 경우
     * NOT_FOUND - 존재하지 않는 유저일 경우, 신용등급이 없는 경우
     * OK - 신용 등급이 미달인 경우
     * CREATED - 성공
     */
    @PostMapping
    fun insertAccount(@RequestBody map: Map<String, String>): ResponseEntity<Account> {
        if (!map.containsKey("ndi")) {
            throw UserException.NullNdiException()
        }
        if (userRepository.findUserByNdi(map.getValue("ndi")) == null) {
            throw UserException.NullUserException()
        }

        val account = accountRepository.findAccountByNdiAndStatus(map.getValue("ndi"), AccountTypeStatus.NORMAL)
        if (account != null) {
            throw AccountException.DuplicationAccountException()
        }

        val userCreditRating = userCreditRatingRepository.findUserCreditRatingByNdi(map.getValue("ndi"))
            ?: throw AccountException.NoCreditRating()

        if (!userCreditRating.isPermit) {
            throw AccountException.BelowCreditRating()
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
    @Transactional(value = "accountTransactionManager")
    @PostMapping("{account-id}/applyments")
    fun updateAccount(
        @PathVariable("account-id") accountId: Int,
        @RequestBody applymentLoanService: ApplymentLoanService?
    ): ResponseEntity<Account> {
        if (applymentLoanService == null) {
            throw AccountException.InvalidApplymentLoanServiceException()
        }
        if (applymentLoanService.amount <= 0) {
            throw AccountException.WrongAmountInput()
        }

        if (accountService.selectAccountByAccountID(accountId).status == AccountTypeStatus.CANCELLED) {
            throw AccountException.CancelledAccountException()
        }

        if (applymentLoanService.type == AccountRequestTypeStatus.DEPOSIT) {
            return ResponseEntity<Account>(
                accountService.depositLoan(accountId, applymentLoanService.amount),
                HttpStatus.CREATED
            )
        }
        if (applymentLoanService.type == AccountRequestTypeStatus.WITHDRAW) {
            return ResponseEntity<Account>(
                accountService.withdrawLoan(accountId, applymentLoanService.amount),
                HttpStatus.CREATED
            )
        } else {
            throw AccountException.UndefinedTypeException()
        }
    }

    /**
     * 전체(account-id) 계좌 정보 리스트조회한다
     *
     * RequestParam : ndi : String
     * ResponseEntity : List<Account>
     * BAD_REQUEST : limit, offset가 잘못 설정된 경우, idType이 잘못 설정된 경우
     * NOT_FOUND : 계좌가 없는 경우, 계좌 거래 내역이 없는 경우
     */
    @GetMapping("transaction")
    fun selectAccountTransactionHistoryList(
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Long,
        @RequestParam(value = "sort-by") sortBy: String?,
        @RequestParam(value = "order-by") orderBy: String?,
        @RequestParam("account-id") accountId: Int?
    ): ResponseEntity<Page<AccountTransactionHistory>> {
        val accountTransactionHistory: Page<AccountTransactionHistory> =
            if (accountId != null) {
                accountService.selectAccountByAccountID(accountId)
                accountTransactionHistoryRepository.findAccountTransactionHistoriesByAccountId(
                    accountId,
                    OffsetBasedPageRequest(limit, offset, sortBy, orderBy, Account.getPrimaryKey())
                )
            } else {
                accountTransactionHistoryRepository.findAll(
                    OffsetBasedPageRequest(limit, offset, sortBy, orderBy, Account.getPrimaryKey())
                )
            }
        if (accountTransactionHistory.content.size == 0) {
            throw AccountException.NullAccountTransactionHistoryException()
        }
        return ResponseEntity<Page<AccountTransactionHistory>>(accountTransactionHistory, HttpStatus.OK)
    }

    /**
     * 계좌 아이디를 입력 받아서 계좌를 해지한다.
     *
     * PathVariable : account-id : Int
     * ResponseEntity : balance : Int, 잔액을 전달
     * BAD_REQUEST : 계좌 정보가 없는 경우
     * NOT_FOUND : 계좌가 이미 해지된 경우
     */
    @DeleteMapping("{account-id}")
    fun removeAccount(@PathVariable("account-id") accountId: Int): ResponseEntity<Nothing> {
        if (accountService.selectAccountByAccountID(accountId).status == AccountTypeStatus.CANCELLED) {
            throw AccountException.CancelledAccountException()
        }
        accountService.removeAccount(accountId)
        return ResponseEntity<Nothing>(HttpStatus.OK)
    }
}
