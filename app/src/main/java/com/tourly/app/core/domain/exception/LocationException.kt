package com.tourly.app.core.domain.exception

sealed class LocationException(val code: String, message: String) : Exception(message) {
    class ProviderError(message: String = "Location provider error", cause: Throwable? = null) : LocationException("LOCATION_PROVIDER_ERROR", message) {
        init { cause?.let { initCause(it) } }
    }
    class PermissionDenied(message: String = "Location permission denied", cause: Throwable? = null) : LocationException("LOCATION_PERMISSION_DENIED", message) {
        init { cause?.let { initCause(it) } }
    }
    class Timeout(message: String = "Location request timed out", cause: Throwable? = null) : LocationException("LOCATION_TIMEOUT", message) {
        init { cause?.let { initCause(it) } }
    }
}
