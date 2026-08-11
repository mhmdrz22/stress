package com.aistudio.detected.stress.agents

import com.aistudio.detected.stress.data.AdviceItem
import com.aistudio.detected.stress.data.local.ChatMessage
import com.aistudio.detected.stress.data.remote.BackendApiClient

object Orchestrator {
    
    // Safety words to trigger immediate offline fallback
    private val crisisKeywords = listOf("خودکشی", "مرگ", "خودزنی", "پایان", "خسته شدم از زندگی", "کشتن", "suicide", "kill")
    
    suspend fun analyze(text: String, history: List<ChatMessage>, deviceId: String): CriticResult {
        // 1. Safety Guard (Local RAG / Safety Critic)
        val isCrisis = crisisKeywords.any { text.contains(it, ignoreCase = true) }
        
        if (isCrisis) {
            return CriticResult(
                hasStress = true,
                category = "crisis",
                empathyMessage = "من متوجه شدم که در شرایط بسیار سختی هستی. لطفاً بدان که تنها نیستی و کمک در دسترس است. لطفاً همین الان با شماره ۱۴۸۰ (صدای مشاور) یا ۱۲۳ (اورژانس اجتماعی) تماس بگیر.",
                adviceList = listOf(AdviceItem("crisis", "تماس فوری با اورژانس اجتماعی", "لطفاً با شماره 123 یا 1480 تماس بگیرید.", "اورژانس")),
                searchKeywords = emptyList(),
                isReliable = true,
                isCrisis = true
            )
        }
        
        // 2. Online Inference (FastAPI -> Hugging Face)
        try {
            val response = BackendApiClient.analyzeChat(deviceId, history, text)
            if (response != null) {
                val hasStress = response.optBoolean("has_stress", false)
                val category = response.optString("category_tag", "general")
                val empathyMessage = response.optString("empathy_message", "من اینجا هستم تا گوش بدم. بیشتر برام بگو.")
                
                val keywordsArray = response.optJSONArray("search_keywords")
                val searchKeywords = mutableListOf<String>()
                if (keywordsArray != null) {
                    for (i in 0 until keywordsArray.length()) {
                        searchKeywords.add(keywordsArray.getString(i))
                    }
                }
                
                return CriticResult(
                    hasStress = hasStress,
                    category = category,
                    empathyMessage = empathyMessage,
                    adviceList = emptyList(), // Can populate from graph if needed
                    searchKeywords = searchKeywords,
                    isReliable = true,
                    isCrisis = false
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // 3. Fallback to Local Agent (Offline Safety Net)
        val perception = PerceptionAgent.analyze(text)
        val rag = LocalRagAgent.retrieveEmpathy(perception)
        val advice = AdviceGraphAgent.getAdvice(perception.category, emptySet())
        
        return CriticAgent.evaluate(perception, rag, advice)
    }
}
