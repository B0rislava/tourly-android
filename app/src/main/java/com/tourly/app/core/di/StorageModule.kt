package com.tourly.app.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.tourly.app.core.storage.TokenManager
import com.tourly.app.core.storage.TokenManagerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import java.security.KeyStore
import androidx.core.content.edit

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tourly_secure_prefs")

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {

    @Binds
    @Singleton
    abstract fun bindTokenManager(
        tokenManagerImpl: TokenManagerImpl
    ): TokenManager

    companion object {

        private const val KEYSET_NAME = "master_keyset"
        private const val KEYSET_PREF_NAME = "master_key_preference"
        private const val MASTER_KEY_URI = "android-keystore://master_key"
        private const val MASTER_KEY_ALIAS = "master_key"

        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
            return context.dataStore
        }

        @Provides
        @Singleton
        fun provideAead(@ApplicationContext context: Context): Aead {
            AeadConfig.register()

            return try {
                getAeadPrimitive(context)
            } catch (e: Exception) {
                // If the key is invalid (e.g. after reinstall), clear everything and recreate
                clearCorruptedKeys(context)
                getAeadPrimitive(context)
            }
        }

        private fun getAeadPrimitive(context: Context): Aead {
            val keysetHandle = AndroidKeysetManager.Builder()
                .withSharedPref(context, KEYSET_NAME, KEYSET_PREF_NAME)
                .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                .withMasterKeyUri(MASTER_KEY_URI)
                .build()
                .keysetHandle


            return keysetHandle.getPrimitive(
                RegistryConfiguration.get(),
                Aead::class.java
            )
        }

        private fun clearCorruptedKeys(context: Context) {
            try {
                // Clear the SharedPreferences containing the keyset
                context.getSharedPreferences(KEYSET_PREF_NAME, Context.MODE_PRIVATE)
                    .edit {
                        clear()
                    }

                // Delete the master key from Android Keystore
                val keyStore = KeyStore.getInstance("AndroidKeyStore")
                keyStore.load(null)
                if (keyStore.containsAlias(MASTER_KEY_ALIAS)) {
                    keyStore.deleteEntry(MASTER_KEY_ALIAS)
                }
            } catch (e: Exception) {
                // Log the error but don't crash
                e.printStackTrace()
            }
        }
    }
}