package com.tourly.app.reviews.domain.repository

import com.tourly.app.core.domain.model.Review
import com.tourly.app.core.network.Result

interface ReviewRepository {
    suspend fun createReview(bookingId: Long, tourRating: Int, guideRating: Int, comment: String?): Result<Review>
    suspend fun getReviewsForTour(tourId: Long): Result<List<Review>>
    suspend fun getReviewsForGuide(guideId: Long): Result<List<Review>>
}
