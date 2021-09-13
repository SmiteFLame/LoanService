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
import org.springframework.retry.annotation.EnableRetry
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import java.net.ConnectException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.sql.Timestamp
import java.util.UUID

@Service
@EnableRetry
class UserServiceImpl : UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userCreditRatingRepository: UserCreditRatingRepository


    @Retryable(maxAttempts = 2, exclude = [UserException.CreditRatingTimeoutException::class])
    override fun searchCreditRating(ndi: String): UserCreditRating {
        // 캐시 메모리에서 UserCreditRating 존재하는지 확인
//        var userCreditRating = findUserCreditRating(ndi)
//        if (userCreditRating != null) {
//            return userCreditRating
//        }

        // 데이터베이스에서 UserCreditRating 존재하는지 확인
        val userCreditRating = userCreditRatingRepository.findUserCreditRatingByNdi(ndi)

        if (userCreditRating != null) {
            return userCreditRating
        }
        return saveCreditRating(ndi, searchGrade(ndi))
    }

    override fun saveCreditRating(ndi: String, creditRatingSearchResult: CreditRatingSearchResult): UserCreditRating {
        val newUserCreditRating = UserCreditRating(
            ndi = ndi,
            grade = creditRatingSearchResult.grade,
            isPermit = creditRatingSearchResult.isPermit,
            createdDate = Timestamp(System.currentTimeMillis())
        )

//        saveUserCreditRating(newUserCreditRating)

        return userCreditRatingRepository.save(newUserCreditRating)
    }

    override fun insertUser(user: User): User {
        user.ndi = UUID.randomUUID().toString()
        return userRepository.save(user)
    }

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
            } else if (response.statusCode() == 502) {
                throw UserException.CreditRatingTimeoutException(
                    response.body().toString(),
                    HttpStatus.valueOf(response.statusCode())
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
