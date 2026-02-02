package com.tourly.app.reviews.domain.usecase

import com.tourly.app.core.domain.model.Review
import com.tourly.app.core.network.Result
import com.tourly.app.reviews.domain.repository.ReviewRepository
import javax.inject.Inject

class CreateReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(
        bookingId: Long,
        tourRating: Int,
        guideRating: Int,
        comment: String?
    ): Result<Review> {
        return reviewRepository.createReview(bookingId, tourRating, guideRating, comment)
    }
}
