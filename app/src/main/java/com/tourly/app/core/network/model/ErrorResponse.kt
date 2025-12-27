package com.tourly.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val code: String,
    val message: String,
    val description: String,
    val errors: Map<String, String>? = null
)
