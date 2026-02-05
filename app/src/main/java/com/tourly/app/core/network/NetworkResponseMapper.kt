package com.tourly.app.core.network

import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import java.net.UnknownHostException

@Serializable
data class BackendErrorResponse(
    val code: String,
    val message: String,
    val description: String? = null,
    val errors: Map<String, String>? = null
)

object NetworkResponseMapper {
    
    suspend inline fun <reified T> map(crossinline call: suspend () -> HttpResponse): Result<T> {
        return try {
            val response = call()
            if (response.status.isSuccess()) {
                val data = response.body<T>()
                Result.Success(data)
            } else {
                // Try to parse error body
                try {
                    val errorBody = response.body<BackendErrorResponse>()
                    Result.Error(
                        code = errorBody.code,
                        message = errorBody.description ?: errorBody.message,
                        errors = errorBody.errors
                    )
                } catch (e: Exception) {
                    // Fallback if parsing fails or body is empty
                    val code = when (response.status.value) {
                        400 -> "BAD_REQUEST"
                        401 -> "UNAUTHORIZED"
                        403 -> "FORBIDDEN"
                        404 -> "NOT_FOUND"
                        in 500..599 -> "SERVER_ERROR"
                        else -> "UNKNOWN_ERROR"
                    }
                    val message = when (response.status.value) {
                        400 -> "Invalid request. Please check your input."
                        401 -> "Invalid credentials or session expired."
                        403 -> "You don't have permission to perform this action."
                        404 -> "The requested resource was not found."
                        in 500..599 -> "Server error. Please try again later."
                        else -> "An unexpected error occurred (${response.status.value})"
                    }
                    Result.Error(code = code, message = message)
                }
            }
        } catch (e: Exception) {
            val code = when (e) {
                is UnknownHostException -> "CONNECTION_ERROR"
                is SocketTimeoutException -> "TIMEOUT_ERROR"
                else -> "NETWORK_ERROR"
            }
            val message = when (e) {
                is UnknownHostException -> "No internet connection. Please check your network settings."
                is SocketTimeoutException -> "The server is taking too long to respond. Please try again."
                else -> e.localizedMessage ?: "Unknown network error"
            }
            Result.Error(code = code, message = message)
        }
    }
}
