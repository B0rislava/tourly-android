package com.tourly.app.core.network.api

import com.tourly.app.core.network.model.CreateTourRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import javax.inject.Inject

class TourApiService @Inject constructor(
    private val client: HttpClient
) {
    suspend fun createTour(token: String, request: CreateTourRequestDto): HttpResponse {
        return client.post("tours") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun getMyTours(token: String): HttpResponse {
        return client.get("tours/my") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}
