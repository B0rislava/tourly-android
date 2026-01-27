package com.tourly.app.create_tour.domain.usecase

import android.content.Context
import com.tourly.app.core.network.Result
import com.tourly.app.create_tour.domain.mapper.CreateTourMapper
import com.tourly.app.create_tour.domain.model.CreateTourParams
import com.tourly.app.create_tour.domain.repository.TourRepository
import com.tourly.app.create_tour.domain.validator.CreateTourValidator
import com.tourly.app.home.domain.model.Tour
import javax.inject.Inject

class UpdateTourUseCase @Inject constructor(
    private val repository: TourRepository,
    private val validator: CreateTourValidator,
    private val mapper: CreateTourMapper,
    private val context: Context
) {
    suspend operator fun invoke(id: Long, params: CreateTourParams): Result<Tour> {
        val validationResult = validator.validate(params)
        
        return if (validationResult.isSuccess) {
            repository.updateTour(context, id, mapper.toDto(params), params.imageUri)
        } else {
            val exception = validationResult.exceptionOrNull()
            Result.Error(
                code = "VALIDATION_ERROR",
                message = exception?.message ?: "Validation failed"
            )
        }
    }
}
