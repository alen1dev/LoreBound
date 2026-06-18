package com.pauls.lorebound.domain.ai

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure storage for AI API keys using EncryptedSharedPreferences.
 */
@Singleton
class AiKeyStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "lorebound_ai_keys",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun getGeminiApiKey(): String? = prefs.getString(KEY_GEMINI, null)?.takeIf { it.isNotBlank() }

    fun setGeminiApiKey(key: String?) {
        prefs.edit().apply {
            if (key.isNullOrBlank()) remove(KEY_GEMINI) else putString(KEY_GEMINI, key)
            apply()
        }
    }

    fun getSelectedProvider(): AiProviderType {
        val name = prefs.getString(KEY_PROVIDER, AiProviderType.DISABLED.name)
        return try {
            AiProviderType.valueOf(name ?: AiProviderType.DISABLED.name)
        } catch (_: Exception) {
            AiProviderType.DISABLED
        }
    }

    fun setSelectedProvider(type: AiProviderType) {
        prefs.edit().putString(KEY_PROVIDER, type.name).apply()
    }

    companion object {
        private const val KEY_GEMINI = "gemini_api_key"
        private const val KEY_PROVIDER = "selected_ai_provider"
    }
}

