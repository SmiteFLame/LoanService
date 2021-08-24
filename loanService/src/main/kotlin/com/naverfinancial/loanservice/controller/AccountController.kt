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
     * 전체 계좌 정보 리스트조회한다
     *
     * RequestParam : ndi : String
     * Bad Request : NDI가 없는 경우
     * ResponseEntity : List<Account>
     */
    @GetMapping()
    fun selectAccountList() : ResponseEntity<List<Account>>{
        try{
            return ResponseEntity<List<Account>>(accountService.selectAccountList(), HttpStatus.OK)
        } catch (err : Exception){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * NDI에 해당되는 계좌 리스트들을 조회한다
     *
     * RequestParam : ndi : String
     * Bad Request : NDI가 없는 경우
     * ResponseEntity : List<Account>
     */
    @GetMapping("ndi/{ndi}")
    fun selectAccountListByNdi(@PathVariable ndi: String) : ResponseEntity<List<Account>>{
        try{
            if(ndi == ""){
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }
            return ResponseEntity<List<Account>>(accountService.selectAccountListByNdi(ndi), HttpStatus.OK)
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
    @GetMapping("{account-id}")
    fun selectAccountByAccountId(@PathVariable("account-id") accountId : Int): ResponseEntity<Account>{
        try{
            return ResponseEntity<Account>(accountService.selectAccountByAccountId(accountId), HttpStatus.OK)
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
    fun insertAccount(@RequestBody map : Map<String, String>) : ResponseEntity<Account>{
        try{
            if(!map.containsKey("ndi") || userService.selectUserByNDI(map.getValue("ndi")) == null){
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }
            var account = accountService.selectAccountByNdiStatusNormal(map.getValue("ndi"))
            if(account != null){
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }

            // 등급 요청은 따로 분리하기
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
    @PutMapping("{account-id}/balance")
    fun updateAccount(@PathVariable("account-id") accountId : Int, @RequestBody detail : Detail) : ResponseEntity<Account>{
        try{
            if(detail.type == "deposit" && detail.amount < 0){
                return ResponseEntity<Account>(accountService.depositLoan(accountId, detail.amount), HttpStatus.CREATED)
            } else if(detail.type == "withdraw" && detail.amount > 0){
                return ResponseEntity<Account>(accountService.withdrawLoan(accountId, detail.amount), HttpStatus.CREATED)
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
    @DeleteMapping("{account-id}")
    fun removeAccount(@PathVariable("account-id") accountId : Int) : ResponseEntity<Integer>{
        try{
            return ResponseEntity<Integer>(accountService.removeAccount(accountId), HttpStatus.CREATED)
        }catch (err : ResponseStatusException){
            return ResponseEntity(err.status)
        }catch (err : Exception){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

}