package com.tourly.app.chat.domain.exception

sealed class ChatException(val code: String, message: String) : Exception(message) {
    class SendFailed(message: String = "Failed to send message") : ChatException("SEND_FAILED", message)
    class ConnectionError(message: String = "WebSocket connection error") : ChatException("CONNECTION_ERROR", message)
    class UserNotFound(message: String = "Current user not found") : ChatException("USER_NOT_FOUND", message)
    class Timeout(message: String = "Timeout waiting for operation") : ChatException("TIMEOUT", message)
}
