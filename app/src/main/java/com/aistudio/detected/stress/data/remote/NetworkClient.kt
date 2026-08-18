package com.aistudio.detected.stress.data.remote

import com.aistudio.detected.stress.data.local.ChatMessage
import org.json.JSONObject

object NetworkClient {
    suspend fun analyzeViaBackend(
        deviceId: String,
        history: List<ChatMessage>,
        currentMessage: String,
        cloudConsentGranted: Boolean = false
    ): JSONObject? {
        if (!cloudConsentGranted) return null
        return null
    }
}
