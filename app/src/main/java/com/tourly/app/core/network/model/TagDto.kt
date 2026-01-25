package com.tourly.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class TagDto(
    val id: Long,
    val name: String,
    val displayName: String
)
