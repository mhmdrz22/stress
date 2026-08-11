package com.aistudio.detected.stress.agents

import com.aistudio.detected.stress.data.AdviceItem
import com.aistudio.detected.stress.data.local.ChatMessage
import com.aistudio.detected.stress.data.remote.NetworkClient
import android.util.Log

object Orchestrator {

    private val crisisKeywords = listOf("خودکشی", "مرگ", "خودزنی", "پایان", "خسته شدم از زندگی", 
        "کشتن", "suicide", "kill myself", "end my life", "die")

    // === LAYER 1: Crisis Guard (Immediate Local) ===
    private fun crisisCheck(text: String): CriticResult? {
        val normalized = text.lowercase()
        if (crisisKeywords.any { normalized.contains(it) }) {
            return CriticResult(
                hasStress = true,
                category = "crisis",
                empathyMessage = "من متوجه شدم که در شرایط بسیار سختی هستی. لطفاً بدان که تنها نیستی.\n\n" +
                    "📞 شماره‌های اورژانس:\n• ۱۴۸۰ - صدای مشاور\n• ۱۲۳ - اورژانس اجتماعی\n• ۰۹۶۰۸۰ - خط ملی اعصاب و روان",
                adviceList = listOf(AdviceItem("crisis", "تماس فوری", "لطفاً با یکی از شماره‌های بالا تماس بگیرید.", "اورژانس")),
                searchKeywords = emptyList(),
                isReliable = true,
                isCrisis = true
            )
        }
        return null
    }

    // === LAYER 2: Local LLM (Ollama/Qwen on server) ===
    private suspend fun tryLocalLLM(deviceId: String, history: List<ChatMessage>, text: String): CriticResult? {
        return try {
            val response = NetworkClient.analyzeLocalLLM(deviceId, history, text)
            parseCloudResponse(response)
        } catch (e: Exception) {
            Log.w("Orchestrator", "Local LLM failed: ${e.message}")
            null
        }
    }

    // === LAYER 3: Cloud HuggingFace ===
    private suspend fun tryHuggingFace(deviceId: String, history: List<ChatMessage>, text: String): CriticResult? {
        return try {
            val response = NetworkClient.analyzeChat(deviceId, history, text)
            parseCloudResponse(response)
        } catch (e: Exception) {
            Log.w("Orchestrator", "HuggingFace failed: ${e.message}")
            null
        }
    }

    // === LAYER 4: Enhanced Local Agent (Neuro-Symbolic) ===
    private fun enhancedLocalAnalysis(text: String, history: List<ChatMessage>): CriticResult {
        val analysis = PerceptionAgent.analyze(text)
        
        val perception = PerceptionResult(
            hasStress = analysis.hasStress,
            severity = analysis.severity,
            category = analysis.category,
            confidence = analysis.confidence,
            isCrisis = analysis.isCrisis
        )
        
        // Use actual RAG engine (Jaccard similarity on dataset)
        val ragResult = LocalRagAgent.retrieveEmpathy(perception)
        
        // Dynamic empathy based on detected keywords + history context
        val empathy = buildDynamicEmpathy(analysis, ragResult, history)
        
        // Personalized advice (deduplicated, history-aware)
        val adviceGraphResult = AdviceGraphAgent.getAdvice(
            category = analysis.category,
            likedTitles = emptyList(), // Should come from DB
            history = history
        )
        
        return CriticAgent.evaluate(
            perception,
            RagResult(empathy),
            adviceGraphResult
        )
    }

    suspend fun analyze(text: String, history: List<ChatMessage>, deviceId: String): CriticResult {
        // LAYER 1: Crisis (always local, immediate)
        crisisCheck(text)?.let { return it }
        
        // LAYER 2: Local LLM (if server has Ollama/DeepSeek)
        tryLocalLLM(deviceId, history, text)?.let { 
            if (it.isReliable) return it 
        }
        
        // LAYER 3: Cloud HuggingFace
        tryHuggingFace(deviceId, history, text)?.let {
            if (it.isReliable) return it
        }
        
        // LAYER 4: Enhanced Local (always works, offline)
        return enhancedLocalAnalysis(text, history)
    }

    private fun parseCloudResponse(response: org.json.JSONObject?): CriticResult? {
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

    private fun buildDynamicEmpathy(
        perception: PerceptionAgent.AnalysisResult,
        ragResult: RagResult?,
        history: List<ChatMessage>
    ): String {
        val baseMessage = ragResult?.empathyMessage ?: when (perception.category) {
            "anxiety" -> "می‌دونم که الان استرس داری. بیا با هم یه نفس عمیق بکشیم."
            "anger" -> "کاملاً درک می‌کنم که عصبانی هستی. حق داری."
            "sleep" -> "بی‌خوابی خسته‌کننده است. می‌خوای بیشتر بگی؟"
            "burnout" -> "خستگی مفرط یعنی خیلی تلاش کردی. الان وقت استراحته."
            "depression" -> "می‌شنوم که روز سختی رو می‌گذرونی. من اینجام."
            "joy" -> "چقدر عالی! خوشحالم که حالت خوبه."
            else -> "من اینجام و گوش می‌دم. بیشتر بگو."
        }
        
        // Add personalization based on history
        val contextHint = if (history.size >= 2) {
            val lastTopic = history.dropLast(1).lastOrNull()?.content?.take(20)
            if (lastTopic != null) " (یادمه قبلاً از $lastTopic صحبت کردیم)" else ""
        } else ""
        
        return baseMessage + contextHint
    }
}
