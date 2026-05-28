package com.tourly.app.core.data.repository

import android.content.Context
import com.tourly.app.core.network.Result
import com.tourly.app.core.network.api.AuthApiService
import com.tourly.app.core.storage.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserRepositoryImplTest {

    private lateinit var tokenManager: TokenManager
    private lateinit var context: Context

    @Before
    fun setup() {
        tokenManager = mockk(relaxed = true)
        context = mockk(relaxed = true)
    }

    private fun createRepositoryWithMockEngine(
        responseContent: String,
        status: HttpStatusCode = HttpStatusCode.OK
    ): UserRepositoryImpl {
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
        val authApiService = AuthApiService(client)
        
        return UserRepositoryImpl(
            authApiService = authApiService,
            tokenManager = tokenManager,
            client = client,
            context = context
        )
    }

    @Test
    fun `getUserProfile returns success and updates flow`() = runTest {
        // Arrange
        val jsonResponse = """
            {
                "id": 1,
                "email": "test@example.com",
                "firstName": "Test",
                "lastName": "User",
                "role": "TRAVELER"
            }
        """.trimIndent()
        
        val repository = createRepositoryWithMockEngine(jsonResponse)

        // Act
        val result = repository.getUserProfile()

        // Assert
        assertTrue(result is Result.Success)
        val user = (result as Result.Success).data
        assertEquals("test@example.com", user.email)
        assertEquals("Test", user.firstName)
        assertEquals("User", user.lastName)

        // Verify flow is updated
        val flowUser = repository.currentUserProfile.first()
        assertEquals(user, flowUser)
    }

    @Test
    fun `logout clears tokens and cache`() = runTest {
        // Arrange
        val repository = createRepositoryWithMockEngine("{}")

        // Act
        repository.logout()

        // Assert
        coVerify { tokenManager.clearToken() }
        coVerify { tokenManager.clearRefreshToken() }
        
        val flowUser = repository.currentUserProfile.first()
        assertEquals(null, flowUser)
    }

    @Test
    fun `deleteAccount clears tokens and returns success`() = runTest {
        // Arrange
        val repository = createRepositoryWithMockEngine("{}", HttpStatusCode.OK)

        // Act
        val result = repository.deleteAccount()

        // Assert
        assertTrue(result is Result.Success)
        coVerify { tokenManager.clearToken() }
        coVerify { tokenManager.clearRefreshToken() }
    }
}
