package com.naverfinancial.loanservice.utils

import java.net.URLEncoder
import java.net.http.HttpRequest

class JsonFormData {
    companion object {
        fun String.utf8(): String = URLEncoder.encode(this, "UTF-8")

        fun formData(data: Map<String, String>): HttpRequest.BodyPublisher? {

            var res = "{\n"
            for ((k, v) in data) {
                res += "\t\"${(k.utf8())}\" : \"${v.utf8()}\" \n"
            }
            res += "}"

            return HttpRequest.BodyPublishers.ofString(res)
        }
    }
}
