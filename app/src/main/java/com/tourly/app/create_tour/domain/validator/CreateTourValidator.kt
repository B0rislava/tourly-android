package com.tourly.app.create_tour.domain.validator

import com.tourly.app.core.util.flatMap
import com.tourly.app.create_tour.domain.exception.CreateTourException
import com.tourly.app.create_tour.domain.model.CreateTourParams
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class CreateTourValidator @Inject constructor(
    private val clock: Clock
) {
    fun validate(params: CreateTourParams): Result<Unit> {
        return validateTitle(params.title)
            .flatMap { validateDescription(params.description) }
            .flatMap { validateLocation(params.location) }
            .flatMap { validateDuration(params.duration) }
            .flatMap { validateGroupSize(params.maxGroupSize) }
            .flatMap { validatePrice(params.pricePerPerson) }
            .flatMap { validateScheduledDate(params.scheduledDate, params.startTime) }
    }

    private fun validateTitle(title: String): Result<Unit> =
        if (title.isBlank()) {
            Result.failure(CreateTourException.InvalidTitle())
        } else {
            Result.success(Unit)
        }

    private fun validateDescription(description: String): Result<Unit> =
        if (description.isBlank()) {
            Result.failure(CreateTourException.InvalidDescription())
        } else {
            Result.success(Unit)
        }

    private fun validateLocation(location: String): Result<Unit> =
        if (location.isBlank()) {
            Result.failure(CreateTourException.InvalidLocation())
        } else {
            Result.success(Unit)
        }

    private fun validateDuration(duration: String): Result<Unit> {
        if (duration.isBlank()) {
            return Result.failure(CreateTourException.InvalidDuration())
        }

        val durationParts = duration.split(":")
        if (durationParts.size != 2) {
            return Result.failure(CreateTourException.InvalidDuration())
        }

        val hours = durationParts[0].toIntOrNull()
        val minutes = durationParts[1].toIntOrNull()

        if (hours == null || minutes == null) {
            return Result.failure(CreateTourException.InvalidDuration())
        }

        if (hours !in 0..23 || minutes !in 0..59) {
            return Result.failure(CreateTourException.InvalidDuration())
        }

        if (hours == 0 && minutes == 0) {
            return Result.failure(CreateTourException.InvalidDurationRange())
        }

        return Result.success(Unit)
    }

    private fun validateGroupSize(maxGroupSize: Int): Result<Unit> =
        if (maxGroupSize < 1) {
            Result.failure(CreateTourException.InvalidGroupSize())
        } else {
            Result.success(Unit)
        }

    private fun validatePrice(pricePerPerson: Double): Result<Unit> =
        if (pricePerPerson <= 0) {
            Result.failure(CreateTourException.InvalidPrice())
        } else {
            Result.success(Unit)
        }

    private fun validateScheduledDate(scheduledDate: LocalDate?, startTime: LocalTime?): Result<Unit> {
        if (scheduledDate == null) {
            return Result.failure(CreateTourException.DateRequired())
        }
        
        if (startTime == null) {
            return Result.failure(CreateTourException.TimeRequired())
        }

        val today = LocalDate.now(clock)
        if (scheduledDate.isBefore(today)) {
            return Result.failure(CreateTourException.DateInPast())
        }
        
        if (scheduledDate.isEqual(today) && startTime != null) {
            val nowTime = LocalTime.now(clock)
            if (startTime.isBefore(nowTime)) {
                return Result.failure(CreateTourException.TimeInPast())
            }
        }

        return Result.success(Unit)
    }
}