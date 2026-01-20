package com.tourly.app.core.network.api

import com.tourly.app.core.network.model.CreateTourRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import javax.inject.Inject

class TourApiService @Inject constructor(
    private val client: HttpClient
) {
    suspend fun createTour(request: CreateTourRequestDto): HttpResponse {
        return client.post("tours") {
            setBody(request)
        }
    }

    suspend fun getMyTours(): HttpResponse {
        return client.get("tours/my")
    }

    suspend fun getAllTours(): HttpResponse {
        return client.get("tours")
    }

    suspend fun getTour(id: Long): HttpResponse {
        return client.get("tours/$id")
    }
}
