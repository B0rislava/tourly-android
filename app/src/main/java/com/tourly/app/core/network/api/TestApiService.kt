package com.tourly.app.core.network.api

import com.tourly.app.core.network.model.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Test API service for verifying backend connectivity
 */
@Singleton
class TestApiService @Inject constructor(
    private val httpClient: HttpClient
) {
    /**
     * Test public endpoint that doesn't require authentication
     */
    suspend fun testPublicEndpoint(): Result<ApiResponse> {
        return try {
            val response = httpClient.get("test/public")
            Result.success(response.body<ApiResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
