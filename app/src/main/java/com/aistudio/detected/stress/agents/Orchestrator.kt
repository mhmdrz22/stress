package com.aistudio.detected.stress.agents

import com.aistudio.detected.stress.data.local.ChatMessage
import com.aistudio.detected.stress.data.remote.BackendApiClient
import android.util.Log
import org.json.JSONObject

object Orchestrator {

    private val crisisKeywords = listOf(
        "خودکشی", "مرگ", "خودزنی", "پایان", "خسته شدم از زندگی",
        "کشتن", "suicide", "kill myself", "end my life", "die", "kill"
    )

    private fun crisisCheck(text: String): CriticResult? {
        val normalized = text.lowercase()
        if (crisisKeywords.any { normalized.contains(it) }) {
            return CriticResult(
                hasStress = true,
                category = "crisis",
                empathyMessage = "من متوجه شدم که در شرایط بسیار سختی هستی. لطفاً بدان که تنها نیستی.\n\n" +
                    "📞 شماره‌های اورژانس:\n• ۱۴۸۰ - صدای مشاور\n• ۱۲۳ - اورژانس اجتماعی",
                adviceList = emptyList(),
                searchKeywords = emptyList(),
                isReliable = true,
                isCrisis = true
            )
        }
        return null
    }

    private suspend fun tryBackend(deviceId: String, history: List<ChatMessage>, text: String): CriticResult? {
        return try {
            val response = BackendApiClient.analyzeChat(deviceId, history, text)
            parseBackendResponse(response)
        } catch (e: Exception) {
            Log.w("Orchestrator", "Backend failed: ${e.message}")
            null
        }
    }

    private fun enhancedLocalAnalysis(text: String, history: List<ChatMessage>): CriticResult {
        val analysis = PerceptionAgent.analyze(text)
        
        val ragResult = LocalRagAgent.retrieveEmpathy(
            PerceptionResult(
                hasStress = analysis.hasStress,
                severity = analysis.severity,
                category = analysis.category,
                confidence = analysis.confidence,
                isCrisis = analysis.isCrisis
            )
        )

        val adviceResult = AdviceGraphAgent.getAdvice(
            category = analysis.category,
            history = history,
            likedTitles = emptyList()
        )

        return CriticAgent.evaluate(
            PerceptionResult(
                hasStress = analysis.hasStress,
                severity = analysis.severity,
                category = analysis.category,
                confidence = analysis.confidence,
                isCrisis = analysis.isCrisis
            ),
            ragResult,
            adviceResult
        )
    }

    suspend fun analyze(text: String, history: List<ChatMessage>, deviceId: String): CriticResult {
        // LAYER 1: Crisis (always local, immediate)
        crisisCheck(text)?.let { return it }
        
        // LAYER 2: Server-side API bypassed to ensure 100% offline local privacy
        // tryBackend(deviceId, history, text)?.let { ... }

        // LAYER 3: Enhanced Local RAG & Rule-based system (100% Offline)
        return enhancedLocalAnalysis(text, history)
    }

    private fun parseBackendResponse(response: JSONObject?): CriticResult? {
        if (response == null) return null
        val hasStress = response.optBoolean("has_stress", false)
        val category = response.optString("category_tag", "general")
        val empathy = response.optString("empathy_message", "")
        val keywordsArray = response.optJSONArray("search_keywords")
        val keywords = mutableListOf<String>()
        keywordsArray?.let {
            for (i in 0 until it.length()) keywords.add(it.getString(i))
        }
        return CriticResult(
            hasStress = hasStress,
            category = category,
            empathyMessage = empathy,
            adviceList = emptyList(),
            searchKeywords = keywords,
            isReliable = category != "general" && empathy.length > 10,
            isCrisis = false
        )
    }
}
