package com.naverfinancial.creditrating.service

import com.naverfinancial.creditrating.datasource.creditRatingSearch.dto.CreditRatingSearchHistory
import com.naverfinancial.creditrating.datasource.creditRatingSearch.dto.CreditRatingSearchResult
import com.naverfinancial.creditrating.datasource.creditRatingSearch.repository.CreditRatingSearchHistoryRepository
import com.naverfinancial.creditrating.datasource.creditRatingSearch.repository.CreditRatingSearchResultRepository
import com.naverfinancial.creditrating.datasource.user.dto.User
import com.naverfinancial.creditrating.exception.UserException
import com.naverfinancial.creditrating.utils.JsonFormData
import org.json.JSONException
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.ConnectException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.sql.Timestamp
import java.time.Duration

@Service
class CreditRatingServiceImpl : CreditRatingService {

    @Autowired
    lateinit var creditRatingSearchHistoryRepository: CreditRatingSearchHistoryRepository

    @Autowired
    lateinit var creditRatingSearchResultRepository: CreditRatingSearchResultRepository

    @Transactional(value = "creditRatingSearchTransactionManager")
    override fun selectGrade(user: User): CreditRatingSearchResult {
        var creditRatingSearchResult = creditRatingSearchResultRepository.findCreditRatingSearchResultByNdi(user.ndi)
        val grade = creditRatingSearchResult?.grade ?: getGrade(user)
//        val grade = getGrade(user)
        val isPermit = evaluateLoanAvailability(grade)

        // CreditRatingSearchHistory 기록하기
        val newCreditRatingSearchHistory =
            CreditRatingSearchHistory(
                historyId = -1, // AUTO_INCREASED
                ndi = user.ndi,
                grade = grade,
                createdDate = Timestamp(System.currentTimeMillis())
            )

        val resultOfCreditRatingSearchHistory = creditRatingSearchHistoryRepository.save(newCreditRatingSearchHistory)

        // CreditRatingSearchResult 기록하기
        creditRatingSearchResult =
            CreditRatingSearchResult(
                ndi = user.ndi,
                grade = grade,
                isPermit = isPermit,
                historyId = resultOfCreditRatingSearchHistory.historyId
            )
        creditRatingSearchResultRepository.save(creditRatingSearchResult)

        return creditRatingSearchResult
    }

    @Transactional(value = "userTransactionManager")
    override fun evaluateLoanAvailability(grade: Int): Boolean {
        // 연체 기록은 현재 없으므로 4단계 아래면 바로 전달
        return grade <= 4
    }

    fun getGrade(user: User): Int {
        try {
            val values = mapOf("age" to user.age.toString(), "salary" to user.salary.toString())
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8888/api/cb/grade"))
                .POST(JsonFormData.formData(values))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            return JSONObject(response.body()).getInt("grade")
        } catch (e: ConnectException) {
            throw UserException.FailConnectCBServerException()
        } catch (e: JSONException) {
            throw UserException.FailRequestCBServerException()
        }
    }
}
