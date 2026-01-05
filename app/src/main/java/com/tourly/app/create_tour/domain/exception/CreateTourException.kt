package com.tourly.app.create_tour.domain.exception

sealed class CreateTourException(message: String) : Exception(message) {
    class InvalidTitle : CreateTourException("Title is required")
    class InvalidDescription : CreateTourException("Description is required")
    class InvalidLocation : CreateTourException("Location is required")
    class InvalidDuration : CreateTourException("Duration must be in HH:mm format")
    class InvalidDurationRange : CreateTourException("Duration must be greater than 0")
    class InvalidGroupSize : CreateTourException("Max group size must be at least 1")
    class InvalidPrice : CreateTourException("Price must be positive")
    class DateRequired : CreateTourException("Date is required")
    class DateInPast : CreateTourException("Date cannot be in the past")
}