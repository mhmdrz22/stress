package com.aistudio.detected.stress.data.remote

import com.aistudio.detected.stress.data.local.ChatMessage
import org.json.JSONObject

/**
 * لایهٔ شبکه برای پردازش ابری اختیاری.
 * در نسخهٔ فعلی هیچ درخواست شبکه‌ای ارسال نمی‌کند.
 */
object NetworkClient {

    suspend fun analyzeViaBackend(
        deviceId: String,
        history: List<ChatMessage>,
        currentMessage: String,
        cloudConsentGranted: Boolean = false
    ): JSONObject? {
        if (!cloudConsentGranted) return null

        /*
         * پیاده‌سازی آینده باید فقط با backend تحت کنترل شما باشد:
         * - HTTPS
         * - توکن کوتاه‌عمر یا session امن
         * - عدم قرار دادن کلید provider در APK
         * - محدودیت retention و logging
         * - حذف یا ناشناس‌سازی متن پیش از ارسال
         */
        return null
    }
}
