package com.tourly.app.core.domain.model

data class PlaceDetails(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val country: String
)
