package com.tourly.app.create_tour.presentation.util

sealed class CreateTourEvent {
    object Success : CreateTourEvent()
    data class Error(val message: String) : CreateTourEvent()
}