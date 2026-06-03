package com.tourly.app.core.domain.exception

sealed class StorageException(val code: String, message: String) : Exception(message) {
    class KeystoreError(message: String = "Error accessing Keystore", cause: Throwable? = null) : StorageException("KEYSTORE_ERROR", message) {
        init { cause?.let { initCause(it) } }
    }
    class TokenSaveFailed(message: String = "Failed to save token", cause: Throwable? = null) : StorageException("TOKEN_SAVE_FAILED", message) {
        init { cause?.let { initCause(it) } }
    }
    class TokenReadFailed(message: String = "Failed to read token", cause: Throwable? = null) : StorageException("TOKEN_READ_FAILED", message) {
        init { cause?.let { initCause(it) } }
    }
    class KeyGenerationFailed(message: String = "Failed to generate encryption key", cause: Throwable? = null) : StorageException("KEY_GENERATION_FAILED", message) {
        init { cause?.let { initCause(it) } }
    }
}
