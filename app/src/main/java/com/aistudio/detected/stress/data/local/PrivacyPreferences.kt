package com.aistudio.detected.stress.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PrivacyPreferences(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val preferences = EncryptedSharedPreferences.create(
        context,
        "aramesh_privacy_preferences",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun isChatHistoryEnabled(): Boolean {
        return preferences.getBoolean(KEY_CHAT_HISTORY_ENABLED, false)
    }

    fun setChatHistoryEnabled(enabled: Boolean) {
        preferences.edit()
            .putBoolean(KEY_CHAT_HISTORY_ENABLED, enabled)
            .apply()
    }

    companion object {
        private const val KEY_CHAT_HISTORY_ENABLED = "chat_history_enabled"
    }
}
