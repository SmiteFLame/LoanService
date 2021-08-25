package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.entity.user.dto.User
import com.naverfinancial.loanservice.entity.user.dto.UserCreditRating
import com.naverfinancial.loanservice.entity.user.repository.UserCreditRatingRepository
import com.naverfinancial.loanservice.entity.user.repository.UserRepository
import com.naverfinancial.loanservice.utils.JsonFormData
import com.naverfinancial.loanservice.wrapper.CreditResult
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.sql.Timestamp
import java.util.*
import java.util.regex.Pattern

@Service
class UserServiceImpl : UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userCreditRatingRepository: UserCreditRatingRepository

    @Qualifier("user")
    @Autowired
    lateinit var userTransactionManager : PlatformTransactionManager

    override fun selectUserByEmails(email: String): User? {
        return userRepository.findUserByEmail(email)
    }

    override fun selectUserByNDI(ndi: String): User? {
        return userRepository.findUserByNdi(ndi)
    }

    override fun selectCreditRating(ndi : String) : UserCreditRating?{
        return userCreditRatingRepository.findUserCreditRatingByNdi(ndi)
    }

    override fun saveCreditRating(ndi : String): UserCreditRating {
        val userCreditRating = selectCreditRating(ndi)

        if(userCreditRating != null){
            return userCreditRating
        }
        var creditResult = searchGrade(ndi)
        val status = userTransactionManager.getTransaction(DefaultTransactionDefinition())

        var newUserCreditRating = UserCreditRating(
            ndi = ndi,
            grade = creditResult.grade,
            isPermit = creditResult.isPermit,
            createdDate = Timestamp(System.currentTimeMillis())
        )
        userCreditRatingRepository.save(newUserCreditRating)
        userTransactionManager.commit(status)
        return newUserCreditRating
    }

    override fun insertUser(user: User): User {
        val status = userTransactionManager.getTransaction(DefaultTransactionDefinition())

        val uuid = UUID.randomUUID().toString()
        user.ndi = uuid

        userRepository.save(user)

        userTransactionManager.commit(status)

        return user
    }


    override fun searchGrade(ndi: String): CreditResult {
        val values = mapOf("ndi" to ndi)
        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/credits"))
            .POST(JsonFormData.formData(values))
            .header("Content-Type", "application/json")
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString());
        val grade = JSONObject(response.body()).getInt("grade")
        val isPermit = JSONObject(response.body()).getBoolean("isPermit")
        return CreditResult(grade, isPermit)
    }
}