package com.tourly.app.create_tour.domain.usecase

import com.tourly.app.core.util.flatMap
import com.tourly.app.create_tour.domain.mapper.CreateTourMapper
import com.tourly.app.create_tour.domain.model.CreateTourParams
import com.tourly.app.create_tour.domain.repository.TourRepository
import com.tourly.app.create_tour.domain.validator.CreateTourValidator
import com.tourly.app.home.domain.model.Tour
import javax.inject.Inject

class CreateTourUseCase @Inject constructor(
    private val repository: TourRepository,
    private val validator: CreateTourValidator,
    private val mapper: CreateTourMapper
) {
    suspend operator fun invoke(params: CreateTourParams): Result<Tour> {
        return validator.validate(params)
            .map { mapper.toDto(params) }
            .flatMap { repository.createTour(it) }
    }
}
