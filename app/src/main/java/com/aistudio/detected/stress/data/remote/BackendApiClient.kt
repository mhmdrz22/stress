package com.aistudio.detected.stress.data.remote

import com.aistudio.detected.stress.data.local.ChatMessage
import org.json.JSONObject

object BackendApiClient {
    /**
     * پردازش ابری به‌صورت پیش‌فرض غیرفعال است.
     * در صورت فعال‌سازی بعدی باید رضایت صریح، endpoint امن،
     * احراز هویت سمت سرور و حداقل‌سازی داده پیاده‌سازی شود.
     */
    suspend fun analyzeChat(
        deviceId: String,
        history: List<ChatMessage>,
        currentMessage: String,
        cloudConsentGranted: Boolean = false
    ): JSONObject? {
        if (!cloudConsentGranted) return null

        // عمداً در این نسخه هیچ متن چت یا کلید API از اپ ارسال نمی‌شود.
        // درخواست cloud باید فقط از طریق backend تحت کنترل شما اجرا شود.
        return null
    }
}
