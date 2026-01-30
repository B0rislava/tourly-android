package com.tourly.app.create_tour.domain.usecase

import com.tourly.app.core.network.Result
import com.tourly.app.create_tour.domain.exception.CreateTourException
import com.tourly.app.create_tour.domain.mapper.CreateTourMapper
import com.tourly.app.create_tour.domain.model.CreateTourParams
import com.tourly.app.create_tour.domain.repository.TourRepository
import com.tourly.app.create_tour.domain.validator.CreateTourValidator
import com.tourly.app.core.domain.model.Tour
import javax.inject.Inject

class CreateTourUseCase @Inject constructor(
    private val repository: TourRepository,
    private val validator: CreateTourValidator,
    private val mapper: CreateTourMapper
) {
    suspend operator fun invoke(params: CreateTourParams): Result<Tour> {
        val validationResult = validator.validate(params)
        
        return if (validationResult.isSuccess) {
            repository.createTour(mapper.toDto(params), params.imageUri?.toString())
        } else {
            val exception = validationResult.exceptionOrNull()
            val code = (exception as? CreateTourException)?.code ?: "VALIDATION_ERROR"
            Result.Error(
                code = code,
                message = exception?.message ?: "Validation failed"
            )
        }
    }
}
