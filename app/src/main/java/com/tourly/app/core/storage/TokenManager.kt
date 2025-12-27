package com.tourly.app.core.storage

import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.crypto.tink.Aead
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface TokenManager {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
}

@Singleton
class TokenManagerImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val aead: Aead
) : TokenManager {

    companion object {
        private val AUTH_TOKEN = stringPreferencesKey("auth_token_encrypted")
    }

    override suspend fun saveToken(token: String) {
        val encryptedBytes = aead.encrypt(token.toByteArray(Charsets.UTF_8), null)
        val encryptedString = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = encryptedString
        }
    }

    override suspend fun getToken(): String? {
        val encryptedString = dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN]
        }.first() ?: return null

        return try {
            val encryptedBytes = Base64.decode(encryptedString, Base64.DEFAULT)
            val decryptedBytes = aead.decrypt(encryptedBytes, null)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN)
        }
    }
}
