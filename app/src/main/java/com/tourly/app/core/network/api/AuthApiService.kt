package com.tourly.app.core.network.api

import com.tourly.app.core.network.model.LoginRequestDto
import com.tourly.app.core.network.model.RegisterRequestDto
import com.tourly.app.core.network.model.UpdateProfileRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
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

    suspend fun getProfile(): HttpResponse {
        return client.get("users/me")
    }

    suspend fun updateProfile(request: UpdateProfileRequestDto): HttpResponse {
        return client.put("users/me") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun uploadProfilePicture(fileBytes: ByteArray): HttpResponse {
        return client.submitFormWithBinaryData(
            url = "users/me/picture",
            formData = formData {
                append("file", fileBytes, Headers.build {
                    append(HttpHeaders.ContentType, "image/*")
                    append(HttpHeaders.ContentDisposition, "filename=\"profile.jpg\"")
                })
            }
        )
    }

    suspend fun deleteProfile(): HttpResponse {
        return client.delete("users/me")
    }

    suspend fun verifyCode(email: String, code: String): HttpResponse {
        return client.post("auth/verify-code") {
            url {
                parameters.append("email", email)
                parameters.append("code", code)
            }
        }
    }

    suspend fun resendCode(email: String): HttpResponse {
        return client.post("auth/resend-code") {
            url {
                parameters.append("email", email)
            }
        }
    }
}
