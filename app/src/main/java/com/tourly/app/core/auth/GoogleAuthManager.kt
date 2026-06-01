package com.tourly.app.core.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.tourly.app.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import com.tourly.app.core.domain.exception.AuthException
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import com.tourly.app.core.network.Result

@Singleton
class GoogleAuthManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val credentialManager = CredentialManager.create(context)

    suspend fun getGoogleIdToken(activityContext: Context): Result<String> {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )
            val credential = result.credential
            
            when {
                credential is GoogleIdTokenCredential -> {
                    Result.Success(credential.idToken)
                }
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    Result.Success(googleIdTokenCredential.idToken)
                }
                else -> Result.Error("AUTH_ERR", "Unknown credential type received")
            }
        } catch (e: GetCredentialCancellationException) {
            Result.Error("AUTH_CANCELED", "Sign-in was cancelled")
        } catch (e: GetCredentialException) {
            val ex = AuthException.GoogleSignInFailed("Credential exception", cause = e)
            Timber.e(ex, "GoogleAuthManager: Get credential failed: ${e.type}")
            Result.Error("CRED_ERR", e.errorMessage?.toString() ?: e.localizedMessage ?: "Device configuration error. Update Google Play Services.")
        } catch (e: Exception) {
            val ex = AuthException.GoogleSignInFailed("Unknown error during sign in", cause = e)
            Timber.e(ex, "GoogleAuthManager: Unknown error")
            Result.Error("AUTH_ERR", e.message ?: "Unknown error during sign in")
        }
    }
}
