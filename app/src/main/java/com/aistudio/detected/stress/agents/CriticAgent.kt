package com.aistudio.detected.stress.agents

import com.aistudio.detected.stress.data.AdviceItem

object CriticAgent {
    /**
     * Agent 4: Safety & Crisis Guard (نگهبان بحران)
     */
    fun evaluate(
        perception: PerceptionResult,
        rag: RagResult,
        advice: AdviceGraphResult
    ): CriticResult {
        
        if (perception.isCrisis) {
            return CriticResult(
                hasStress = true,
                category = "crisis",
                empathyMessage = rag.empathyMessage + "\nلطفاً در صورت نیاز به کمک فوری با شماره 1480 (صدای مشاور) یا 123 (اورژانس اجتماعی) تماس بگیر.",
                adviceList = emptyList(), // No generic advice during crisis
                searchKeywords = emptyList(),
                isReliable = true,
                isCrisis = true
            )
        }
        
        val finalHasStress = if (perception.confidence < 0.4f) false else perception.hasStress
        
        var finalMessage = rag.empathyMessage
        if (perception.confidence < 0.5f && finalHasStress) {
            finalMessage = "برداشت من اینه که ممکنه کمی استرس داشته باشی، اما مطمئن نیستم. $finalMessage"
        }
        
        val finalAdvice = if (finalHasStress) advice.adviceList else emptyList()
        val finalKeywords = if (finalHasStress) advice.searchKeywords else emptyList()
        
        return CriticResult(
            hasStress = finalHasStress,
            category = perception.category,
            empathyMessage = finalMessage,
            adviceList = finalAdvice,
            searchKeywords = finalKeywords,
            isReliable = perception.confidence >= 0.5f,
            isCrisis = false
        )
    }
}

data class PerceptionResult(
    val hasStress: Boolean,
    val severity: Int,
    val category: String,
    val confidence: Float,
    val isCrisis: Boolean
)

data class CriticResult(
    val hasStress: Boolean,
    val category: String,
    val empathyMessage: String,
    val adviceList: List<AdviceItem>,
    val searchKeywords: List<String>,
    val isReliable: Boolean,
    val isCrisis: Boolean = false
)
