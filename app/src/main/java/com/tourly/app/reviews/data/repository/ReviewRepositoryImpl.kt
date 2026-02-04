package com.tourly.app.reviews.data.repository

import com.tourly.app.BuildConfig
import com.tourly.app.core.domain.model.Review
import com.tourly.app.core.network.Result
import com.tourly.app.reviews.data.dto.CreateReviewRequest
import com.tourly.app.reviews.data.dto.ReviewDto
import com.tourly.app.reviews.domain.repository.ReviewRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import com.tourly.app.core.network.NetworkResponseMapper
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
        return when (val result = NetworkResponseMapper.map<ReviewDto> {
            httpClient.post("${BuildConfig.BASE_URL}reviews") {
                contentType(ContentType.Application.Json)
                setBody(CreateReviewRequest(bookingId, tourRating, guideRating, comment))
            }
        }) {
            is Result.Success -> Result.Success(mapDtoToDomain(result.data))
            is Result.Error -> result
        }
    }

    override suspend fun getReviewsForTour(tourId: Long): Result<List<Review>> {
        return when (val result = NetworkResponseMapper.map<List<ReviewDto>> {
            httpClient.get("${BuildConfig.BASE_URL}reviews/tours/$tourId")
        }) {
            is Result.Success -> Result.Success(result.data.map { mapDtoToDomain(it) })
            is Result.Error -> result
        }
    }

    override suspend fun getReviewsForGuide(guideId: Long): Result<List<Review>> {
        return when (val result = NetworkResponseMapper.map<List<ReviewDto>> {
            httpClient.get("${BuildConfig.BASE_URL}reviews/guides/$guideId")
        }) {
            is Result.Success -> Result.Success(result.data.map { mapDtoToDomain(it) })
            is Result.Error -> result
        }
    }

    override suspend fun getMyReviews(): Result<List<Review>> {
        return when (val result = NetworkResponseMapper.map<List<ReviewDto>> {
            httpClient.get("${BuildConfig.BASE_URL}reviews/my")
        }) {
            is Result.Success -> Result.Success(result.data.map { mapDtoToDomain(it) })
            is Result.Error -> result
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
            createdAt = try { LocalDateTime.parse(dto.createdAt) } catch (e: Exception) { LocalDateTime.now() },
            tourTitle = dto.tourTitle
        )
    }
}
