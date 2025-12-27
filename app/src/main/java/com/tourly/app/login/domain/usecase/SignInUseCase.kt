package com.tourly.app.login.domain.usecase

import com.tourly.app.core.network.model.LoginResponse
import com.tourly.app.login.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<LoginResponse> {
        return repository.signIn(email, password)
    }
}
