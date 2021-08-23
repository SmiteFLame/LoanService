package com.naverfinancial.loanservice.controller

import com.naverfinancial.loanservice.entity.account.dto.Account
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
    fun searchAll(@RequestParam("ndi") ndi: String) : ResponseEntity<List<Account>>{
        try{
            if(ndi == ""){
                return ResponseEntity<List<Account>>(accountService.searchByNdi(ndi), HttpStatus.OK)
            }
            return ResponseEntity<List<Account>>(accountService.searchAll(), HttpStatus.OK)
        } catch (err : Exception){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("{account-numbers}")
    fun searchByAccountNumber(@PathVariable("account-numbers") accountNumbers : String): ResponseEntity<Account>{
        try{
            return ResponseEntity<Account>(accountService.searchByAccountNumbers(accountNumbers), HttpStatus.OK)
        }catch (err : ResponseStatusException){
            return ResponseEntity(err.status)
        }catch (err : Exception){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PostMapping()
    fun openAccount(@RequestBody map : Map<String, String>) : ResponseEntity<Account>{
        try{
            if(!map.containsKey("ndi") || userService.searchUserByNDI(map.getValue("ndi")) == null){
                // 존재하지 않는 NDI
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }
            var creditResult = accountService.searchGrade(map.getValue("ndi"))

            if(!creditResult.isPermit){
                // 신용등급 미달 (Status상태 수정 예정)
                return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
            }

            return ResponseEntity<Account>(accountService.openAccount(map.getValue("ndi"), creditResult), HttpStatus.CREATED)
        }catch (err : Exception){
            // 클라이언트가 오류가 아니라면 서버 오류로 보내야 된다.
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PutMapping("{account-numbers}/balance")
    fun applicationLoan(@PathVariable("account-numbers") accountNumbers: String, @RequestBody detail : Detail) : ResponseEntity<Account>{
        try{
            if(detail.type.equals("deposit")){
                return ResponseEntity<Account>(accountService.depositLoan(accountNumbers, detail.amount), HttpStatus.CREATED)
            } else if(detail.type.equals("withdraw")){
                return ResponseEntity<Account>(accountService.withdrawLoan(accountNumbers, detail.amount), HttpStatus.CREATED)
            } else{
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }
        }catch (err : ResponseStatusException){
            return ResponseEntity(err.status)
        }catch (err : Exception){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @DeleteMapping("{account-numbers}")
    fun cancelAccount(@PathVariable("account-numbers") accountNumbers: String) : ResponseEntity<Integer>{
        try{
            return ResponseEntity<Integer>(accountService.cancelAccount(accountNumbers), HttpStatus.CREATED)
        }catch (err : ResponseStatusException){
            return ResponseEntity(err.status)
        }catch (err : Exception){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

}