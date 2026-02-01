package com.tourly.app.core.domain.usecase

import com.tourly.app.core.domain.repository.UserRepository
import com.tourly.app.core.domain.model.User
import javax.inject.Inject
import com.tourly.app.core.network.Result

class UpdateProfilePictureUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(uriString: String): Result<User> {
        return repository.uploadProfilePicture(uriString)
    }
}
