package com.tourly.app.core.domain.exception

sealed class AuthException(val code: String, message: String) : Exception(message) {
    class GoogleSignInFailed(message: String = "Google Sign-In failed", cause: Throwable? = null) : AuthException("GOOGLE_SIGN_IN_FAILED", message) {
        init { cause?.let { initCause(it) } }
    }
    class TokenRevoked(message: String = "Auth token has been revoked", cause: Throwable? = null) : AuthException("TOKEN_REVOKED", message) {
        init { cause?.let { initCause(it) } }
    }
    class LoginFailed(message: String = "Login failed", cause: Throwable? = null) : AuthException("LOGIN_FAILED", message) {
        init { cause?.let { initCause(it) } }
    }
}
