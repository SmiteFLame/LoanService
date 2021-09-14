package com.naverfinancial.loanservice.service

import com.naverfinancial.loanservice.cache.UserCreditRatingCache
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
import java.net.http.HttpTimeoutException
import java.sql.Timestamp
import java.time.Duration
import java.util.UUID

@Service
@EnableRetry
class UserServiceImpl : UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userCreditRatingRepository: UserCreditRatingRepository

    override fun searchCreditRating(ndi: String): UserCreditRating {
        val creditRatingSearchResult = searchGrade(ndi)

        val userCreditRating = UserCreditRating(
            ndi = ndi,
            grade = creditRatingSearchResult.grade,
            isPermit = creditRatingSearchResult.isPermit,
            createdDate = Timestamp(System.currentTimeMillis())
        )

        UserCreditRatingCache.insertCache(ndi, userCreditRating)

        return userCreditRatingRepository.save(userCreditRating)
    }

    override fun insertUser(user: User): User {
        user.ndi = UUID.randomUUID().toString()
        return userRepository.save(user)
    }

    @Retryable(maxAttempts = 2, exclude = [UserException.CreditRatingTimeoutException::class, HttpTimeoutException::class])
    override fun searchGrade(ndi: String): CreditRatingSearchResult {
        try {
            val values = mapOf("ndi" to ndi)
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/credits"))
                .POST(JsonFormData.formData(values))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .build()
            // "chunked transfer encoding, state: READING_LENGTH" 에러가 발생하는 지역
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
        } catch (e: HttpTimeoutException) {
            throw UserException.CreditRatingTimeoutException()
        }
    }
}
