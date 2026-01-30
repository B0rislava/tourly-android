package com.tourly.app.home.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TagDto(
    val id: Long,
    val name: String,
    val displayName: String
)
