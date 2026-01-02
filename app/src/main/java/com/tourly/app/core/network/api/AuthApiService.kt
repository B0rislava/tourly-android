package com.tourly.app.core.network.api

import com.tourly.app.core.network.model.LoginRequestDto
import com.tourly.app.core.network.model.RegisterRequestDto
import com.tourly.app.core.network.model.UpdateProfileRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders

class AuthApiService @Inject constructor(
    private val client: HttpClient
) {

    suspend fun login(request: LoginRequestDto): HttpResponse {
        return client.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun register(request: RegisterRequestDto): HttpResponse {
        return client.post("auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun getProfile(token: String): HttpResponse {
        return client.get("users/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    suspend fun updateProfile(token: String, request: UpdateProfileRequestDto): HttpResponse {
        return client.put("users/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}
