package com.tourly.app.login.domain.usecase

import com.tourly.app.core.network.Result
import com.tourly.app.core.domain.model.User
import com.tourly.app.login.domain.UserRole
import com.tourly.app.login.domain.repository.AuthRepository
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String, role: UserRole? = null): Result<User> {
        return repository.googleSignIn(idToken, role)
    }
}
