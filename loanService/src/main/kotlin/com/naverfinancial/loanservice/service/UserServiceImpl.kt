package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.entity.user.dto.User
import com.naverfinancial.loanservice.entity.user.dto.UserCreditRating
import com.naverfinancial.loanservice.entity.user.repository.UserCreditRatingRepository
import com.naverfinancial.loanservice.entity.user.repository.UserRepository
import com.naverfinancial.loanservice.exception.CreditRatingException
import com.naverfinancial.loanservice.utils.JsonFormData
import com.naverfinancial.loanservice.wrapper.CreditRatingSearchResult
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.DefaultTransactionDefinition
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.sql.Timestamp
import java.util.*

@Service
class UserServiceImpl : UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userCreditRatingRepository: UserCreditRatingRepository

    override fun selectUserByEmails(email: String): User? {
        return userRepository.findUserByEmail(email)
    }

    override fun selectUserByNDI(ndi: String): User? {
        return userRepository.findUserByNdi(ndi)
    }

    override fun selectCreditRating(ndi: String): UserCreditRating? {
        return userCreditRatingRepository.findUserCreditRatingByNdi(ndi)
    }

    override fun saveCreditRating(ndi: String): UserCreditRating {
        val userCreditRating = selectCreditRating(ndi)

        if (userCreditRating != null) {
            return userCreditRating
        }
        var creditRatingSearchResult = searchGrade(ndi)

        var newUserCreditRating = UserCreditRating(
            ndi = ndi,
            grade = creditRatingSearchResult.grade,
            isPermit = creditRatingSearchResult.isPermit,
            createdDate = Timestamp(System.currentTimeMillis())
        )

        return userCreditRatingRepository.save(newUserCreditRating)
    }

    override fun insertUser(user: User): User {
        val uuid = UUID.randomUUID().toString()
        user.ndi = uuid

        return  userRepository.save(user)
    }


    override fun searchGrade(ndi: String): CreditRatingSearchResult {
        val values = mapOf("ndi" to ndi)
        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/credits"))
            .POST(JsonFormData.formData(values))
            .header("Content-Type", "application/json")
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() == 200){
            return CreditRatingSearchResult(
                JSONObject(response.body()).getInt("grade"),
                JSONObject(response.body()).getBoolean("isPermit")
            )
        } else {
            throw CreditRatingException(response.body().toString(), HttpStatus.valueOf(response.statusCode()))
        }
    }
}
