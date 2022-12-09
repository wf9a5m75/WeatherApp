package com.example.weatherapp.mock

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.net.HttpURLConnection

object MockDispatcher : Dispatcher() {

    override fun dispatch(request: RecordedRequest): MockResponse {
        return when (request.path) {
            "/api/v1/locations" -> getLocations(request)

            else -> MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
        }
    }

    private fun getLocations(request: RecordedRequest): MockResponse {
        if (request.headers.get("If-None-Match") == "etag1") {
            return MockResponse()
                .addHeader("X-Test-Type", "ETag")
                .setResponseCode(HttpURLConnection.HTTP_NOT_MODIFIED)
        }

        return MockResponse()
            .addHeader("ETag: etag1")
            .setResponseCode(200)
            .addHeader("X-Test-Type", "Without ETag")
            .setBody("""
                {
                  "last_update": "2022-11-01T00:00",
                  "prefectures": [
                    {
                      "id": "osaka",
                      "name": "大阪府",
                      "cities": [
                        {
                          "name": "枚方市",
                          "id": "osaka_hirakata"
                        },
                        {
                          "name": "大阪市",
                          "id": "osaka_osaka"
                        },
                        {
                          "name": "堺市",
                          "id": "osaka_sakai"
                        },
                        {
                          "name": "関西国際空港",
                          "id": "osaka_kansai-airport"
                        }
                      ]
                    },

                    {
                      "id": "hyogo",
                      "name": "兵庫県",
                      "cities": [
                        {
                          "name": "姫路市",
                          "id": "hyogo_himeji"
                        },
                        {
                          "name": "神戸市",
                          "id": "hyogo_kobe"
                        },
                        {
                          "name": "丹波市",
                          "id": "hyogo_tamba"
                        }
                      ]
                    }
                  ]
                }
            """.trimIndent())

    }
}
