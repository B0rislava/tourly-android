package com.tourly.app.login.domain.usecase

import com.tourly.app.core.network.Result
import com.tourly.app.login.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        resetCode: String,
        newPassword: String
    ): Result<Unit> {
        return repository.resetPassword(email, resetCode, newPassword)
    }
}
