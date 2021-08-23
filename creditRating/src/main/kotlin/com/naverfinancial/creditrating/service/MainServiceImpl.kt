package com.naverfinancial.creditrating.service

import com.naverfinancial.creditrating.entity.creditRatingSearch.dto.CreditRatingSearchHistory
import com.naverfinancial.creditrating.entity.creditRatingSearch.dto.CreditRatingSearchResult
import com.naverfinancial.creditrating.entity.creditRatingSearch.repository.CreditRatingSearchHistoryRepository
import com.naverfinancial.creditrating.entity.creditRatingSearch.repository.CreditRatingSearchResultRepository
import com.naverfinancial.creditrating.entity.user.repository.UserRespository
import com.naverfinancial.creditrating.utils.JsonFormData
import com.naverfinancial.creditrating.wrapper.CreditResult
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.DefaultTransactionDefinition
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.sql.Timestamp
import java.time.Duration

@Service
class MainServiceImpl : MainService {

    @Autowired
    lateinit var userRespository: UserRespository

    @Autowired
    lateinit var creditRatingSearchHistoryRepository: CreditRatingSearchHistoryRepository

    @Autowired
    lateinit var creditRatingSearchResultRepository: CreditRatingSearchResultRepository


    override fun selectGrade(NDI: String): CreditResult {
        var user = userRespository.findUserByNDI(NDI)

        if (user == null) {
            // 에러 처리
        }

        // CB 모듈에서 가져오는 기능은 다른 utils으로 옮기기
        val values = mapOf("age" to user.age.toString(), "salary" to user.salary.toString())
        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8888/api/cb/grade"))
            .POST(JsonFormData.formData(values))
            .timeout(Duration.ofSeconds(10))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString());
        val grade = JSONObject(response.body()).getInt("grade")
        val isPermit = evaluateLoanAvailability(grade)

        // CreaditRatingSearchHistory 기록하기
        val newCreditRatingSearchHistory =
            CreditRatingSearchHistory(
                historyId = -1, // AUTO_INCREASED
                NDI = user.NDI,
                grade = grade,
                createdDate = Timestamp(System.currentTimeMillis())
            )

        val resultOfCreditRatingSearchHistory = creditRatingSearchHistoryRepository.save(newCreditRatingSearchHistory)

        // CreaditRatingSearchResult 기록하기
        val newCreditRatingSearchResult =
            CreditRatingSearchResult(
                NDI = user.NDI,
                grade = grade,
                historyId = resultOfCreditRatingSearchHistory.historyId
            )
        creditRatingSearchResultRepository.save(newCreditRatingSearchResult)

        return CreditResult(grade, isPermit)
    }

    override fun evaluateLoanAvailability(grade: Int): Boolean {
        // 연체 기록은 현재 없으므로 4단계 아래면 바로 전달
        return grade <= 4
    }
}