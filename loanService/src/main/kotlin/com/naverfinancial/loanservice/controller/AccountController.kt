package com.naverfinancial.loanservice.controller

import com.naverfinancial.loanservice.dto.Account
import com.naverfinancial.loanservice.wrapper.Detail
import com.naverfinancial.loanservice.dto.User
import com.naverfinancial.loanservice.service.AccountService
import com.naverfinancial.loanservice.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/accounts")
class AccountController{

    @Autowired
    lateinit var accountService : AccountService

    @Autowired
    lateinit var userService : UserService

    @GetMapping()
    fun searchAll(@RequestParam("NDI") NDI: String) : ResponseEntity<List<Account>>{
        if(NDI.equals("")){
            return ResponseEntity<List<Account>>(accountService.searchAll(), HttpStatus.OK)
        }
        // 만약 조건이 있다면 전체 조회
        return ResponseEntity<List<Account>>(accountService.searchAll(), HttpStatus.OK)
    }

    @GetMapping("{account-numbers}")
    fun serarchByAccountNumber(@PathVariable("account_numbers") account_numbers : String): ResponseEntity<Optional<Account>>{
        return ResponseEntity<Optional<Account>>(accountService.searchByAccountNumbers(account_numbers), HttpStatus.OK)
    }

    @GetMapping("{NDI}/NDI")
    fun serarchByNDI(@PathVariable NDI : String): ResponseEntity<List<Account>>{
        return ResponseEntity<List<Account>>(accountService.searchByNDI(NDI), HttpStatus.OK)
    }

    @PostMapping()
    fun openAccount(@RequestBody NDI : String) : ResponseEntity<Account>{
        if(userService.searchUserByNDI(NDI).isEmpty()){
            // 존재하지 않는 NDI
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        var creditResult = accountService.searchGrade(NDI)
        if(creditResult.getIsPermit()){
            // 신용등급 미달 (Status상태 수정 예정)
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity<Account>(accountService.openAccount(NDI, creditResult), HttpStatus.OK)
    }

    @PutMapping("{account_numbers}/balance")
    fun applicationLoan(@PathVariable account_numbers: String, @RequestBody detail : Detail) : ResponseEntity<Optional<Account>>{
        if(detail.getType().equals("desposit")){
            return ResponseEntity<Optional<Account>>(accountService.depositLoan(account_numbers, detail.getAmount()), HttpStatus.OK)
        } else if(detail.getType().equals("withdraw")){
            return ResponseEntity<Optional<Account>>(accountService.withdrawLoan(account_numbers, detail.getAmount()), HttpStatus.OK)
        } else{
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @DeleteMapping("{account-numbers}")
    fun cancelAccount(@PathVariable("account-numbers") account_numbers: String){

    }

}