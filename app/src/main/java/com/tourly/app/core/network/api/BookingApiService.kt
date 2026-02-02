package com.tourly.app.core.network.api

import com.tourly.app.home.data.dto.BookTourRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class BookingApiService @Inject constructor(
    private val client: HttpClient
) {
    suspend fun bookTour(request: BookTourRequestDto): HttpResponse {
        return client.post("bookings") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun getMyBookings(): HttpResponse {
        return client.get("bookings/my")
    }

    suspend fun getGuideBookings(): HttpResponse {
        return client.get("bookings/guide")
    }

    suspend fun cancelBooking(id: Long): HttpResponse {
        return client.post("bookings/$id/cancel")
    }

    suspend fun completeBooking(id: Long): HttpResponse {
        return client.post("bookings/$id/complete")
    }
}
