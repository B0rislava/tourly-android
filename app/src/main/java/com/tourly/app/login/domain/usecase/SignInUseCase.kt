package com.tourly.app.login.domain.usecase

import com.tourly.app.core.network.model.LoginResponseDto
import com.tourly.app.login.domain.repository.AuthRepository
import javax.inject.Inject
import com.tourly.app.core.network.Result

class SignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<LoginResponseDto> {
        return repository.signIn(email, password)
    }
}
