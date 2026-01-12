package com.tourly.app.core.storage

import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.crypto.tink.Aead
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface TokenManager {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
    fun getTokenFlow(): Flow<String?>
    suspend fun saveRefreshToken(token: String)
    suspend fun getRefreshToken(): String?
    suspend fun clearRefreshToken()
    suspend fun saveTokens(accessToken: String, refreshToken: String)
}

@Singleton
class TokenManagerImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val aead: Aead
) : TokenManager {

    companion object {
        private val AUTH_TOKEN = stringPreferencesKey("auth_token_encrypted")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token_encrypted")
    }

    override suspend fun saveToken(token: String) {
        try {
            println("TokenManager: Starting token save (length: ${token.length})...")
            val encryptedBytes = aead.encrypt(token.toByteArray(Charsets.UTF_8), null)
            val encryptedString = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)

            // Wait for the write to complete
            dataStore.edit { preferences ->
                preferences[AUTH_TOKEN] = encryptedString
            }

            val savedToken = getToken()
                ?: throw Exception("Token verification failed - token not found after save")

            println("TokenManager: Token saved and verified successfully (length: ${savedToken.length})")

        } catch (e: Exception) {
            println("TokenManager: Failed to save token: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun getToken(): String? {
        return try {
            val encryptedString = dataStore.data.map { preferences ->
                preferences[AUTH_TOKEN]
            }.first()

            if (encryptedString == null) {
                println("TokenManager: No token found in storage")
                return null
            }

            val encryptedBytes = Base64.decode(encryptedString, Base64.DEFAULT)
            val decryptedBytes = aead.decrypt(encryptedBytes, null)
            val token = String(decryptedBytes, Charsets.UTF_8)
            println("TokenManager: Token retrieved successfully (length: ${token.length})")
            token
        } catch (e: Exception) {
            // CRITICAL: If decryption fails (corrupted data), clear the token
            println("TokenManager: Decryption failed - clearing corrupted token: ${e.message}")
            e.printStackTrace()

            // Clear the corrupted data
            clearToken()

            // Return null so the app treats the user as logged out
            null
        }
    }

    override suspend fun clearToken() {
        try {
            dataStore.edit { preferences ->
                preferences.remove(AUTH_TOKEN)
            }
            println("TokenManager: Token cleared from storage")
        } catch (e: Exception) {
            println("TokenManager: Failed to clear token: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun getTokenFlow(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN]
        }.map { encryptedString ->
            if (encryptedString == null) {
                 null
            } else {
                try {
                    val encryptedBytes = Base64.decode(encryptedString, Base64.DEFAULT)
                    val decryptedBytes = aead.decrypt(encryptedBytes, null)
                    String(decryptedBytes, Charsets.UTF_8)
                } catch (e: Exception) {
                    // If decryption fails in the Flow, we can't call clearToken()
                    // (it would cause infinite loop), so just return null
                    println("TokenManager: Flow decryption failed: ${e.message}")
                    e.printStackTrace()
                    null
                }
            }
        }
    }

    override suspend fun saveRefreshToken(token: String) {
        try {
            println("TokenManager: Starting refresh token save (length: ${token.length})...")
            val encryptedBytes = aead.encrypt(token.toByteArray(Charsets.UTF_8), null)
            val encryptedString = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)

            dataStore.edit { preferences ->
                preferences[REFRESH_TOKEN] = encryptedString
            }
            println("TokenManager: Refresh token saved successfully")
        } catch (e: Exception) {
            println("TokenManager: Failed to save refresh token: ${e.message}")
            e.printStackTrace()
        }
    }

    override suspend fun getRefreshToken(): String? {
        return try {
            val encryptedString = dataStore.data.map { preferences ->
                preferences[REFRESH_TOKEN]
            }.first() ?: return null.also { println("TokenManager: No refresh token found") }

            val encryptedBytes = Base64.decode(encryptedString, Base64.DEFAULT)
            val decryptedBytes = aead.decrypt(encryptedBytes, null)
            val token = String(decryptedBytes, Charsets.UTF_8)
            println("TokenManager: Refresh token retrieved successfully (length: ${token.length})")
            token
        } catch (e: Exception) {
            println("TokenManager: Failed to decrypt refresh token: ${e.message}")
            clearRefreshToken()
            null
        }
    }

    override suspend fun clearRefreshToken() {
        try {
            dataStore.edit { preferences ->
                preferences.remove(REFRESH_TOKEN)
            }
        } catch (e: Exception) {
            println("TokenManager: Failed to clear refresh token: ${e.message}")
        }
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        try {
            println("TokenManager: Starting atomic token save...")
            val encryptedTokenBytes = aead.encrypt(accessToken.toByteArray(Charsets.UTF_8), null)
            val encryptedTokenString = Base64.encodeToString(encryptedTokenBytes, Base64.DEFAULT)

            val encryptedRefreshBytes = aead.encrypt(refreshToken.toByteArray(Charsets.UTF_8), null)
            val encryptedRefreshString = Base64.encodeToString(encryptedRefreshBytes, Base64.DEFAULT)

            dataStore.edit { preferences ->
                preferences[AUTH_TOKEN] = encryptedTokenString
                preferences[REFRESH_TOKEN] = encryptedRefreshString
            }
            println("TokenManager: Atomic token save complete. Access len: ${accessToken.length}, Refresh len: ${refreshToken.length}")
        } catch (e: Exception) {
            println("TokenManager: Failed to save tokens atomically: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}
