package com.naverfinancial.creditrating.service

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

@Service
class MainServiceImpl : MainService {

    @Autowired
    lateinit var userRespository: UserRespository

    override fun selectGrade(NDI : String) : CreditResult{
        var user = userRespository.findUserByNDI(NDI)

        if(user == null){
            // 에러 처리
        }

        val values = mapOf("age" to user.age.toString(), "salary" to user.salary.toString())
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

        // CreaditRatingSearchResult 기록하기


        return CreditResult(grade, isPermit)
    }

    override fun evaluateLoanAvailability(grade: Int): Boolean {
        // 연체 기록은 현재 없으므로 4단계 아래면 바로 전달
        return grade <= 4
    }
}