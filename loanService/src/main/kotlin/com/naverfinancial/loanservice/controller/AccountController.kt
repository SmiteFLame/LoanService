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
            return ResponseEntity<List<Account>>(accountService.searchByNDI(NDI), HttpStatus.OK)
        }
        return ResponseEntity<List<Account>>(accountService.searchAll(), HttpStatus.OK)
    }

    @GetMapping("{account-numbers}")
    fun serarchByAccountNumber(@PathVariable("account-numbers") accountNumbers : String): ResponseEntity<Optional<Account>>{
        return ResponseEntity<Optional<Account>>(accountService.searchByAccountNumbers(accountNumbers), HttpStatus.OK)
    }

    @PostMapping()
    fun openAccount(@RequestBody map : Map<String, String>) : ResponseEntity<Account>{
        if(!map.containsKey("NDI") || userService.searchUserByNDI(map.getValue("NDI")).isEmpty()){
            // 존재하지 않는 NDI
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        var creditResult = accountService.searchGrade(map.getValue("NDI"))
        if(creditResult.getIsPermit()){
            // 신용등급 미달 (Status상태 수정 예정)
            return ResponseEntity(HttpStatus.METHOD_NOT_ALLOWED)
        }

        return ResponseEntity<Account>(accountService.openAccount(map.getValue("NDI"), creditResult), HttpStatus.OK)
    }

    @PutMapping("{account-numbers}/balance")
    fun applicationLoan(@PathVariable("account-numbers") accountNumbers: String, @RequestBody detail : Detail) : ResponseEntity<Optional<Account>>{
        if(detail.getType().equals("desposit")){
            return ResponseEntity<Optional<Account>>(accountService.depositLoan(accountNumbers, detail.getAmount()), HttpStatus.OK)
        } else if(detail.getType().equals("withdraw")){
            return ResponseEntity<Optional<Account>>(accountService.withdrawLoan(accountNumbers, detail.getAmount()), HttpStatus.OK)
        } else{
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @DeleteMapping("{account-numbers}")
    fun cancelAccount(@PathVariable("account-numbers") accountNumbers: String) : ResponseEntity<Boolean>{
        cancelAccount(accountNumbers)

        // 잔액이 남아 있다면
        return ResponseEntity(HttpStatus.METHOD_NOT_ALLOWED)
    }

}