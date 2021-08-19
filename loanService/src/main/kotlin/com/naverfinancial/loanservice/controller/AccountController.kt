package com.naverfinancial.loanservice.controller

import com.naverfinancial.loanservice.dto.Account
import com.naverfinancial.loanservice.wrapper.Detail
import com.naverfinancial.loanservice.service.AccountService
import com.naverfinancial.loanservice.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
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
        try{
            if(NDI.equals("")){
                return ResponseEntity<List<Account>>(accountService.searchByNDI(NDI), HttpStatus.OK)
            }
            return ResponseEntity<List<Account>>(accountService.searchAll(), HttpStatus.OK)
        } catch (err : Exception){
            return ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
        }
    }

    @GetMapping("{account-numbers}")
    fun serarchByAccountNumber(@PathVariable("account-numbers") accountNumbers : String): ResponseEntity<Optional<Account>>{
        try{
            return ResponseEntity<Optional<Account>>(accountService.searchByAccountNumbers(accountNumbers), HttpStatus.OK)
        }catch (err : ResponseStatusException){
            return ResponseEntity(err.status)
        }catch (err : Exception){
            return ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
        }
    }

    @PostMapping()
    fun openAccount(@RequestBody map : Map<String, String>) : ResponseEntity<Account>{
        try{
            if(!map.containsKey("NDI") || userService.searchUserByNDI(map.getValue("NDI")).isEmpty){
                // 존재하지 않는 NDI
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }

            var creditResult = accountService.searchGrade(map.getValue("NDI"))
            if(creditResult.getIsPermit()){
                // 신용등급 미달 (Status상태 수정 예정)
                return ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            }

            return ResponseEntity<Account>(accountService.openAccount(map.getValue("NDI"), creditResult), HttpStatus.CREATED)
        }catch (err : Exception){
            return ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
        }
    }

    @PutMapping("{account-numbers}/balance")
    fun applicationLoan(@PathVariable("account-numbers") accountNumbers: String, @RequestBody detail : Detail) : ResponseEntity<Account>{
        try{
            if(detail.getType().equals("deposit")){
                return ResponseEntity<Account>(accountService.depositLoan(accountNumbers, detail.getAmount()), HttpStatus.CREATED)
            } else if(detail.getType().equals("withdraw")){
                return ResponseEntity<Account>(accountService.withdrawLoan(accountNumbers, detail.getAmount()), HttpStatus.CREATED)
            } else{
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }
        }catch (err : ResponseStatusException){
            return ResponseEntity(err.status)
        }catch (err : Exception){
            return ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
        }
    }

    @DeleteMapping("{account-numbers}")
    fun cancelAccount(@PathVariable("account-numbers") accountNumbers: String) : ResponseEntity<Boolean>{
        try{
            return ResponseEntity<Boolean>(accountService.cancelAccount(accountNumbers), HttpStatus.CREATED)
        }catch (err : ResponseStatusException){
            return ResponseEntity(err.status)
        }catch (err : Exception){
            return ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
        }
    }

}