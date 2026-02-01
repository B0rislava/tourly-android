package com.tourly.app.core.domain.model

data class ReverseGeocodedAddress(
    val fullAddress: String,
    val city: String,
    val country: String,
    val displayLocation: String
)
