package com.tourly.app.core.domain.exception

sealed class UserException(val code: String, message: String) : Exception(message) {
    class ImageReadError(message: String = "Failed to read image file", cause: Throwable? = null) : UserException("FILE_ERROR", message) {
        init { cause?.let { initCause(it) } }
    }
    class ProfileNotFound(message: String = "User profile not found", cause: Throwable? = null) : UserException("PROFILE_NOT_FOUND", message) {
        init { cause?.let { initCause(it) } }
    }
}
