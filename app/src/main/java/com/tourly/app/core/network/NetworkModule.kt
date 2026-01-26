package com.tourly.app.core.network

import com.tourly.app.core.storage.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess
import com.tourly.app.core.network.model.RefreshTokenRequestDto
import com.tourly.app.core.network.model.RefreshTokenResponseDto
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import com.tourly.app.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(tokenManager: TokenManager): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        println("HTTP Client: loadTokens called - attempting to load from storage...")
                        val accessToken = tokenManager.getToken()
                        val refreshToken = tokenManager.getRefreshToken()
                        println("HTTP Client: loadTokens - Access: ${accessToken?.take(10)}..., Refresh: ${refreshToken?.take(10)}...")
                        if (accessToken != null && refreshToken != null) {
                            println("HTTP Client: loadTokens - Returning valid tokens")
                            BearerTokens(accessToken, refreshToken)
                        } else {
                            println("HTTP Client: loadTokens - Returning NULL tokens (Access=${accessToken != null}, Refresh=${refreshToken != null})")
                            null
                        }
                    }
                    
                    sendWithoutRequest { request ->
                        request.url.host == BuildConfig.BASE_HOST && 
                        !request.url.encodedPath.endsWith("/auth/refresh") &&
                        !request.url.encodedPath.endsWith("/auth/login") &&
                        !request.url.encodedPath.endsWith("/auth/register")
                    }

                    refreshTokens {
                        val refreshToken = tokenManager.getRefreshToken() ?: return@refreshTokens null
                        try {
                            val refreshClient = HttpClient(Android) {
                                install(ContentNegotiation) {
                                     json(Json { ignoreUnknownKeys = true })
                                }
                            }
                            
                            try {
                                val refreshResponse = refreshClient.post(BuildConfig.BASE_URL + "auth/refresh") {
                                    contentType(ContentType.Application.Json)
                                    setBody(RefreshTokenRequestDto(refreshToken))
                                }

                                if (refreshResponse.status.isSuccess()) {
                                    val tokens = refreshResponse.body<RefreshTokenResponseDto>()
                                    tokenManager.saveToken(tokens.accessToken)
                                    tokenManager.saveRefreshToken(tokens.refreshToken)
                                    BearerTokens(tokens.accessToken, tokens.refreshToken)
                                } else {
                                    tokenManager.clearToken()
                                    tokenManager.clearRefreshToken()
                                    null
                                }
                            } finally {
                                refreshClient.close()
                            }
                        } catch (e: Exception) {
                            println("HTTP Client: Failed to refresh token: ${e.message}")
                            tokenManager.clearToken()
                            tokenManager.clearRefreshToken()
                            null
                        }
                    }
                }
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("HTTP Client: $message")
                    }
                }
                level = LogLevel.INFO
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 120000  // 2 minutes for full request (includes image upload to Cloudinary)
                connectTimeoutMillis = 60000   // 1 minute to establish connection
                socketTimeoutMillis = 60000    // 1 minute for socket read/write operations
            }

            defaultRequest {
                url(BuildConfig.BASE_URL)
                contentType(ContentType.Application.Json)
            }
        }
    }
}