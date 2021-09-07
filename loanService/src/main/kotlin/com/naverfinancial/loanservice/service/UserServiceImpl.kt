package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.datasource.user.dto.User
import com.naverfinancial.loanservice.datasource.user.dto.UserCreditRating
import com.naverfinancial.loanservice.datasource.user.repository.UserCreditRatingRepository
import com.naverfinancial.loanservice.datasource.user.repository.UserRepository
import com.naverfinancial.loanservice.exception.UserException
import com.naverfinancial.loanservice.utils.JsonFormData
import com.naverfinancial.loanservice.wrapper.CreditRatingSearchResult
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.ConnectException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.sql.Timestamp
import java.util.UUID

@Service
class UserServiceImpl : UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userCreditRatingRepository: UserCreditRatingRepository

    @Transactional(value = "userTransactionManager")
    override fun saveCreditRating(ndi: String): UserCreditRating {

        val userCreditRating = userCreditRatingRepository.findUserCreditRatingByNdi(ndi)

        if (userCreditRating != null) {
            return userCreditRating
        }

        val creditRatingSearchResult = searchGrade(ndi)

        val newUserCreditRating = UserCreditRating(
            ndi = ndi,
            grade = creditRatingSearchResult.grade,
            isPermit = creditRatingSearchResult.isPermit,
            createdDate = Timestamp(System.currentTimeMillis())
        )

        return userCreditRatingRepository.save(newUserCreditRating)
    }

    @Transactional(value = "userTransactionManager")
    override fun insertUser(user: User): User {
        user.ndi = UUID.randomUUID().toString()
        return userRepository.save(user)
    }

    @Transactional(value = "userTransactionManager")
    override fun searchGrade(ndi: String): CreditRatingSearchResult {
        try {
            val values = mapOf("ndi" to ndi)
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/credits"))
                .POST(JsonFormData.formData(values))
                .header("Content-Type", "application/json")
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() == 200) {
                return CreditRatingSearchResult(
                    JSONObject(response.body()).getInt("grade"),
                    JSONObject(response.body()).getBoolean("isPermit")
                )
            } else {
                throw UserException.CreditRatingException(
                    response.body().toString(),
                    HttpStatus.valueOf(response.statusCode())
                )
            }
        } catch (e: ConnectException) {
            throw UserException.FailConnectCreditRatingServerException()
        }
    }
}
