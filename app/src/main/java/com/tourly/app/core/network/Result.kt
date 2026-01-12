package com.tourly.app.core.network

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(
        val code: String,
        val message: String,
        val errors: Map<String, String>? = null
    ) : Result<Nothing>()
}
