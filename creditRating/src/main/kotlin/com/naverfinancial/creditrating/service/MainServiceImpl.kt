package com.naverfinancial.creditrating.service

import com.naverfinancial.creditrating.dto.CreditRatingSearchHistory
import com.naverfinancial.creditrating.dto.CreditRatingSearchResult
import com.naverfinancial.creditrating.repository.CreditRatingSearchHistoryRepository
import com.naverfinancial.creditrating.repository.CreditRatingSearchResultRepository
import com.naverfinancial.creditrating.repository.UserRespository
import com.naverfinancial.creditrating.utils.JsonFormData
import com.naverfinancial.creditrating.wrapper.CreditResult
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.sql.Timestamp

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

        // 이미 검색된 결과가 존재하면 가져오기
        //if(creditRatingSearchResultRepository.findCreditRatingSearchResultByNDI(user.getNDI()) != null){

        //}

        // CB 모듈에서 가져오는 기능은 다른 utils으로 옮기기
        val values = mapOf("age" to user.getAge().toString(), "salary" to user.getSalary().toString())
        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8888/api/cb/grade"))
            .POST(JsonFormData.formData(values))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString());
        val grade = JSONObject(response.body()).getInt("grade")
        val isPermit = evaluateLoanAvailability(grade)

        // CreaditRatingSearchHistory 기록하기
        var newCreditRatingSearchHistory =
            CreditRatingSearchHistory(
                history_id = -1, // AUTO_INCREASED
                NDI = user.getNDI(),
                grade = grade,
                created_date = Timestamp(System.currentTimeMillis())
            )
        var resultOfCreditRatingSearchHistory = creditRatingSearchHistoryRepository.save(newCreditRatingSearchHistory)

        println(resultOfCreditRatingSearchHistory.getHistoryId())
        // CreaditRatingSearchResult 기록하기
        var newCreditRatingSearchResult =
            CreditRatingSearchResult(
                NDI = user.getNDI(),
                grade = grade,
                history_id = resultOfCreditRatingSearchHistory.getHistoryId()
            )
        creditRatingSearchResultRepository.save(newCreditRatingSearchResult)

        return CreditResult(grade, isPermit)
    }

    override fun evaluateLoanAvailability(grade: Int): Boolean {
        // 연체 기록은 현재 없으므로 4단계 아래면 바로 전달
        return grade <= 4
    }
}