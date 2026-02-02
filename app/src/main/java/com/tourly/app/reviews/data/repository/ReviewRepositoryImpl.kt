package com.tourly.app.reviews.data.repository

import com.tourly.app.BuildConfig
import com.tourly.app.core.domain.model.Review
import com.tourly.app.core.network.Result
import com.tourly.app.reviews.data.dto.CreateReviewRequest
import com.tourly.app.reviews.data.dto.ReviewDto
import com.tourly.app.reviews.domain.repository.ReviewRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient
) : ReviewRepository {

    override suspend fun createReview(
        bookingId: Long,
        tourRating: Int,
        guideRating: Int,
        comment: String?
    ): Result<Review> {
        return try {
            val response = httpClient.post("${BuildConfig.BASE_URL}reviews") {
                contentType(ContentType.Application.Json)
                setBody(CreateReviewRequest(bookingId, tourRating, guideRating, comment))
            }
            if (response.status.value in 200..299) {
                val dto = response.body<ReviewDto>()
                Result.Success(mapDtoToDomain(dto))
            } else {
                Result.Error(code = response.status.value.toString(), message = "Failed to create review")
            }
        } catch (e: Exception) {
            Result.Error(code = e.javaClass.simpleName, message = e.message ?: "Unknown error")
        }
    }

    override suspend fun getReviewsForTour(tourId: Long): Result<List<Review>> {
        return try {
            val response = httpClient.get("${BuildConfig.BASE_URL}reviews/tours/$tourId")
            if (response.status.value in 200..299) {
                val dtos = response.body<List<ReviewDto>>()
                Result.Success(dtos.map { mapDtoToDomain(it) })
            } else {
                Result.Error(code = response.status.value.toString(), message = "Failed to fetch reviews")
            }
        } catch (e: Exception) {
            Result.Error(code = e.javaClass.simpleName, message = e.message ?: "Unknown error")
        }
    }

    override suspend fun getReviewsForGuide(guideId: Long): Result<List<Review>> {
        return try {
            // Updated endpoint based on backend controller: /reviews/guides/{guideId}
            val response = httpClient.get("${BuildConfig.BASE_URL}reviews/guides/$guideId")
            if (response.status.value in 200..299) {
                val dtos = response.body<List<ReviewDto>>()
                Result.Success(dtos.map { mapDtoToDomain(it) })
            } else {
                 Result.Error(code = response.status.value.toString(), message = "Failed to fetch reviews")
            }
        } catch (e: Exception) {
            Result.Error(code = e.javaClass.simpleName, message = e.message ?: "Unknown error")
        }
    }

    private fun mapDtoToDomain(dto: ReviewDto): Review {
        return Review(
            id = dto.id,
            bookingId = null, // DTO doesn't strictly need to return bookingId for list views
            reviewerName = dto.reviewerName,
            reviewerProfilePicture = dto.reviewerProfilePicture,
            tourRating = dto.tourRating,
            guideRating = dto.guideRating,
            comment = dto.comment,
            createdAt = try { LocalDateTime.parse(dto.createdAt) } catch (e: Exception) { LocalDateTime.now() }
        )
    }
}
