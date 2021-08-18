package com.naverfinancial.creditrating.controller

import com.naverfinancial.creditrating.service.MainService
import com.naverfinancial.creditrating.wrapper.CreditResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin("*")
@RequestMapping("/credits")
class MainController {


    @Autowired
    lateinit var mService : MainService

    @PostMapping("")
    fun selectGrade(@RequestBody map : Map<String,String>): ResponseEntity<CreditResult>{
        if(!map.containsKey("NDI")){
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity<CreditResult>(mService.selectGrade(map.getValue("NDI")), HttpStatus.OK)

    }
}