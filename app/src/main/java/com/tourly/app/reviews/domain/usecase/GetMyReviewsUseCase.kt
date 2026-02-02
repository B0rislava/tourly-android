package com.tourly.app.reviews.domain.usecase

import com.tourly.app.core.domain.model.Review
import com.tourly.app.core.network.Result
import com.tourly.app.reviews.domain.repository.ReviewRepository
import javax.inject.Inject

class GetMyReviewsUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(): Result<List<Review>> {
        return reviewRepository.getMyReviews()
    }
}
