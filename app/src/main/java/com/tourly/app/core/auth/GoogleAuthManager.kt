package com.tourly.app.core.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.tourly.app.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import com.tourly.app.core.domain.exception.AuthException
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAuthManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val credentialManager = CredentialManager.create(context)

    suspend fun getGoogleIdToken(): String? {
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
                context = context
            )
            val credential = result.credential
            
            when {
                credential is GoogleIdTokenCredential -> {
                    credential.idToken
                }
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    googleIdTokenCredential.idToken
                }
                else -> null
            }
        } catch (e: GetCredentialException) {
            val ex = AuthException.GoogleSignInFailed("Credential exception", cause = e)
            Timber.e(ex, "GoogleAuthManager: Get credential failed")
            null
        } catch (e: Exception) {
            val ex = AuthException.GoogleSignInFailed("Unknown error during sign in", cause = e)
            Timber.e(ex, "GoogleAuthManager: Unknown error")
            null
        }
    }
}
