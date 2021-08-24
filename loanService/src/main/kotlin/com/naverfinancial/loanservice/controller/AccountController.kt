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

    /**
     * 전체 계좌 정보, NDI에 해당되는 계좌 정보들을 조회한다
     *
     * RequestParam : ndi : String
     * Bad Request : NDI가 없는 경우
     * ResponseEntity : List<Account>
     */
    @GetMapping()
    fun searchAll(@RequestParam("ndi") ndi: String) : ResponseEntity<List<Account>>{
        try{
            if(ndi != ""){
                if(userService.searchUserByNDI(ndi) == null){
                    return ResponseEntity(HttpStatus.BAD_REQUEST)
                }
                return ResponseEntity<List<Account>>(accountService.searchByNdi(ndi), HttpStatus.OK)
            }
            return ResponseEntity<List<Account>>(accountService.searchAll(), HttpStatus.OK)
        } catch (err : Exception){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * 계좌 번호을 입력받아서 계좌 정보를 조회한다
     *
     * PathVariable : ndi : String
     * ResponseEntity : Account
     * GATEWAY_TIMEOUT - 10초 이내로 데이터 요청을 신용등급을 못 가져온 경우
     */
    @GetMapping("{account-numbers}")
    fun searchByAccountNumber(@PathVariable("account-numbers") accountNumbers : String): ResponseEntity<Account>{
        try{
            return ResponseEntity<Account>(accountService.searchByAccountNumbers(accountNumbers), HttpStatus.OK)
        }catch (err : Exception){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * NDI을 입력받아서 마이너스 계좌를 개설한다, 이미 계좌가 존재하는 경우 그 계좌를 반환한다
     *
     * RequestBody : ndi : String
     * ResponseEntity : Account
     * BAD_REQUEST - ndi가 RequestBody에 없을 경우, User에 해당되는 ndi가 없는 경우
     */
    @PostMapping()
    fun openAccount(@RequestBody map : Map<String, String>) : ResponseEntity<Account>{
        try{
            if(!map.containsKey("ndi") || userService.searchUserByNDI(map.getValue("ndi")) == null){
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }
            var creditResult = accountService.searchGrade(map.getValue("ndi"))

            if(!creditResult.isPermit){
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }

            return ResponseEntity<Account>(accountService.openAccount(map.getValue("ndi"), creditResult), HttpStatus.CREATED)
        }catch (err : Exception){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * 계좌번호를 입력받아서 대출을 신청 및 반환한다.
     *
     * PathVariable : account-numbers : String
     * RequestBody : type(대출 신청/반환 종류), amount(신청 금액)
     * ResponseEntity : Account
     * BAD_REQUEST - type이 잘못 된 경우, 계좌가 존재하지 않는 경우, 통장이 정지된 경우, 한도보다 더 많은 금액을 대출 신청 한 경우
     */
    @PutMapping("{account-numbers}/balance")
    fun applicationLoan(@PathVariable("account-numbers") accountNumbers: String, @RequestBody detail : Detail) : ResponseEntity<Account>{
        try{
            if(detail.type.equals("deposit") && detail.amount < 0){
                return ResponseEntity<Account>(accountService.depositLoan(accountNumbers, detail.amount), HttpStatus.CREATED)
            } else if(detail.type.equals("withdraw") && detail.amount > 0){
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

    /**
     * 계좌정보를 입력 받아서 계좌를 해지한다.
     *
     * PathVariable : account-numbers : String
     * ResponseEntity : balance : Int, 잔액을 전달
     * BAD_REQUEST - 계좌 정보가 없는 경우, 계좌가 이미 해지된 경우, 계좌에 잔고가 마이너스 인 경우
     */
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