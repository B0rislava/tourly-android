package com.tourly.app.core.domain.usecase

import com.tourly.app.core.domain.repository.UserRepository
import com.tourly.app.core.network.Result
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return userRepository.deleteAccount()
    }
}
