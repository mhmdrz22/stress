package com.aistudio.detected.stress.agents

import com.aistudio.detected.stress.data.AdviceItem

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

object CriticAgent {
    fun evaluate(
        perception: PerceptionResult,
        rag: RagResult,
        advice: AdviceGraphResult
    ): CriticResult {
        if (perception.isCrisis) {
            return CriticResult(
                hasStress = true,
                category = "crisis",
                empathyMessage = SafetyGate.urgentMessage(),
                adviceList = emptyList(),
                searchKeywords = emptyList(),
                isReliable = true,
                isCrisis = true
            )
        }

        val reliable = perception.confidence >= 0.5f
        val hasStress = perception.hasStress && perception.confidence >= 0.3f

        val message = when {
            hasStress && !reliable ->
                "ممکن است تحت فشار یا استرس باشی، اما مطمئن نیستم. ${rag.empathyMessage}"

            else -> rag.empathyMessage
        }

        return CriticResult(
            hasStress = hasStress,
            category = perception.category,
            empathyMessage = message,
            adviceList = if (hasStress) advice.adviceList else emptyList(),
            searchKeywords = if (hasStress) advice.searchKeywords else emptyList(),
            isReliable = reliable,
            isCrisis = false
        )
    }
}
