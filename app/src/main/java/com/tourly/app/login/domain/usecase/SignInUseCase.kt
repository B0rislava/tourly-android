package com.tourly.app.login.domain.usecase

import com.tourly.app.login.domain.repository.AuthRepository
import javax.inject.Inject
import com.tourly.app.core.network.Result
import com.tourly.app.core.domain.model.User

class SignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return repository.signIn(email, password)
    }
}
