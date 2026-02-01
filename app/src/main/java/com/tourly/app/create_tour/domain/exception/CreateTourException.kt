package com.tourly.app.create_tour.domain.exception

sealed class CreateTourException(val code: String, message: String) : Exception(message) {
    class InvalidTitle : CreateTourException("INVALID_TITLE", "Title is required")
    class InvalidDescription : CreateTourException("INVALID_DESCRIPTION", "Description is required")
    class InvalidLocation : CreateTourException("INVALID_LOCATION", "Location is required")
    class InvalidDuration : CreateTourException("INVALID_DURATION", "Duration must be in HH:mm format")
    class InvalidDurationRange : CreateTourException("INVALID_DURATION_RANGE", "Duration must be greater than 0")
    class InvalidGroupSize : CreateTourException("INVALID_GROUP_SIZE", "Max group size must be at least 1")
    class InvalidPrice : CreateTourException("INVALID_PRICE", "Price must be positive")
    class DateRequired : CreateTourException("DATE_REQUIRED", "Date is required")
    class DateInPast : CreateTourException("DATE_IN_PAST", "Date cannot be in the past")
}