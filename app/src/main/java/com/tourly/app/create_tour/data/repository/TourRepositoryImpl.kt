package com.tourly.app.create_tour.data.repository

import com.tourly.app.core.network.api.TourApiService
import com.tourly.app.core.network.model.CreateTourRequestDto
import com.tourly.app.core.network.model.CreateTourResponseDto
import com.tourly.app.core.network.model.ErrorResponse
import com.tourly.app.core.storage.TokenManager
import com.tourly.app.create_tour.domain.repository.TourRepository
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import javax.inject.Inject

class TourRepositoryImpl @Inject constructor(
    private val apiService: TourApiService,
    private val tokenManager: TokenManager
) : TourRepository {

    override suspend fun createTour(request: CreateTourRequestDto): Result<CreateTourResponseDto> {
        return try {
            val token = tokenManager.getToken() ?: return Result.failure(Exception("No authentication token found"))
            
            val response = apiService.createTour(token, request)
            
            if (response.status.isSuccess()) {
                val createResponse = response.body<CreateTourResponseDto>()
                Result.success(createResponse)
            } else {
                val errorResponse = try {
                    response.body<ErrorResponse>()
                } catch (e: Exception) {
                    null
                }

                // TODO: Handle exceptions
                
                val errorMessage = errorResponse?.description 
                    ?: errorResponse?.message 
                    ?: "Failed to create tour: ${response.status}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyTours(): Result<List<CreateTourResponseDto>> {
        return try {
            val token = tokenManager.getToken() ?: return Result.failure(Exception("No authentication token found"))

            val response = apiService.getMyTours(token)

            if (response.status.isSuccess()) {
                val tours = response.body<List<CreateTourResponseDto>>()
                Result.success(tours)
            } else {
                val errorResponse = try {
                    response.body<ErrorResponse>()
                } catch (e: Exception) {
                    null
                }

                val errorMessage = errorResponse?.description
                    ?: errorResponse?.message
                    ?: "Failed to get tours: ${response.status}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
