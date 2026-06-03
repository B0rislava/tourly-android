package com.tourly.app.core.data.repository

import com.tourly.app.core.mapper.BookingMapper
import com.tourly.app.core.domain.model.Booking
import com.tourly.app.core.network.Result
import com.tourly.app.core.network.api.BookingApiService
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BookingRepositoryImplTest {

    private lateinit var mapper: BookingMapper

    @Before
    fun setup() {
        mapper = mockk()
    }

    private fun createRepositoryWithMockEngine(
        responseContent: String,
        status: HttpStatusCode = HttpStatusCode.OK
    ): BookingRepositoryImpl {
        val mockEngine = MockEngine { _ ->
            respond(
                content = responseContent,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        val apiService = BookingApiService(client)
        
        return BookingRepositoryImpl(
            apiService = apiService,
            mapper = mapper
        )
    }

    @Test
    fun `getMyBookings returns success and mapped data`() = runTest {
        // Arrange
        val jsonResponse = """
            [
                {
                    "id": 1,
                    "tourId": 10,
                    "tourTitle": "Test Tour",
                    "userId": 5,
                    "numberOfParticipants": 2,
                    "totalPrice": 100.0,
                    "status": "PENDING",
                    "createdAt": "2024-05-28T10:00:00"
                }
            ]
        """.trimIndent()
        
        val repository = createRepositoryWithMockEngine(jsonResponse)
        val mockBooking = mockk<Booking>()
        every { mapper.toDomain(any()) } returns mockBooking

        // Act
        val result = repository.getMyBookings()

        // Assert
        assertTrue(result is Result.Success)
        val bookings = (result as Result.Success).data
        assertEquals(1, bookings.size)
        assertEquals(mockBooking, bookings.first())
    }

    @Test
    fun `cancelBooking returns success when api call succeeds`() = runTest {
        // Arrange
        val repository = createRepositoryWithMockEngine("{}", HttpStatusCode.OK)

        // Act
        val result = repository.cancelBooking(1L)

        // Assert
        assertTrue(result is Result.Success)
    }

    @Test
    fun `bookTour returns error when api call fails`() = runTest {
        // Arrange
        val errorResponse = """{"message": "Not found"}"""
        val repository = createRepositoryWithMockEngine(errorResponse, HttpStatusCode.NotFound)

        // Act
        val result = repository.bookTour(1L, 2)

        // Assert
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertEquals("NOT_FOUND", error.code)
    }
}
