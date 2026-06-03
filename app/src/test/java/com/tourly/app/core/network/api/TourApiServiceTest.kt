package com.tourly.app.core.network.api

import com.tourly.app.core.domain.model.TourFilters
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

class TourApiServiceTest {

    private fun createServiceWithMock(
        mockResponse: String = "{}",
        expectedStatus: HttpStatusCode = HttpStatusCode.OK,
        expectedPath: String,
        expectedMethod: HttpMethod
    ): TourApiService {
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
        return TourApiService(client)
    }

    @Test
    fun `getMyTours returns success response`() = runTest {
        val service = createServiceWithMock(
            expectedPath = "/tours/my",
            expectedMethod = HttpMethod.Get
        )
        val response = service.getMyTours()
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `getToursByGuideId returns success response`() = runTest {
        val guideId = 123L
        val service = createServiceWithMock(
            expectedPath = "/tours/guide/$guideId",
            expectedMethod = HttpMethod.Get
        )
        val response = service.getToursByGuideId(guideId)
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `getAllTags returns success response`() = runTest {
        val service = createServiceWithMock(
            expectedPath = "/tags",
            expectedMethod = HttpMethod.Get
        )
        val response = service.getAllTags()
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `getAllTours returns success response`() = runTest {
        val filters = TourFilters()
        val service = createServiceWithMock(
            expectedPath = "/tours",
            expectedMethod = HttpMethod.Get
        )
        val response = service.getAllTours(filters)
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `getTour returns success response`() = runTest {
        val id = 456L
        val service = createServiceWithMock(
            expectedPath = "/tours/$id",
            expectedMethod = HttpMethod.Get
        )
        val response = service.getTour(id)
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `deleteTour returns success response`() = runTest {
        val id = 789L
        val service = createServiceWithMock(
            expectedPath = "/tours/$id",
            expectedMethod = HttpMethod.Delete
        )
        val response = service.deleteTour(id)
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `toggleSaveTour returns success response`() = runTest {
        val id = 101L
        val service = createServiceWithMock(
            expectedPath = "/tours/$id/toggle-save",
            expectedMethod = HttpMethod.Post
        )
        val response = service.toggleSaveTour(id)
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `getSavedTours returns success response`() = runTest {
        val service = createServiceWithMock(
            expectedPath = "/tours/saved",
            expectedMethod = HttpMethod.Get
        )
        val response = service.getSavedTours()
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
