package com.tourly.app.core.network.api

import com.tourly.app.login.data.dto.LoginRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthApiServiceTest {

    private fun createServiceWithMock(
        mockResponse: String = "{}",
        expectedStatus: HttpStatusCode = HttpStatusCode.OK,
        expectedPath: String,
        expectedMethod: HttpMethod
    ): AuthApiService {
        val mockEngine = MockEngine { request ->
            assertEquals(expectedPath, request.url.encodedPath)
            assertEquals(expectedMethod, request.method)
            respond(
                content = mockResponse,
                status = expectedStatus,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        return AuthApiService(client)
    }

    @Test
    fun `login returns success response`() = runTest {
        // Arrange
        val service = createServiceWithMock(
            expectedPath = "/auth/login",
            expectedMethod = HttpMethod.Post
        )
        val requestDto = LoginRequestDto("test@example.com", "password")

        // Act
        val response = service.login(requestDto)

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `getProfile returns success response`() = runTest {
        // Arrange
        val service = createServiceWithMock(
            expectedPath = "/users/me",
            expectedMethod = HttpMethod.Get
        )

        // Act
        val response = service.getProfile()

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `deleteProfile returns success response`() = runTest {
        // Arrange
        val service = createServiceWithMock(
            expectedPath = "/users/me",
            expectedMethod = HttpMethod.Delete
        )

        // Act
        val response = service.deleteProfile()

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
