package com.tourly.app.core.network.api

import com.tourly.app.core.network.model.LoginRequest
import com.tourly.app.core.network.model.RegisterRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject
import io.ktor.client.statement.HttpResponse

class AuthApiService @Inject constructor(
    private val client: HttpClient
) {

    suspend fun login(request: LoginRequest): HttpResponse {
        return client.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun register(request: RegisterRequest): HttpResponse {
        return client.post("auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}
