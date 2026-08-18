package com.aistudio.detected.stress.agents

import com.aistudio.detected.stress.data.local.ChatMessage

object Orchestrator {

    suspend fun analyze(
        text: String,
        history: List<ChatMessage>,
        deviceId: String
    ): CriticResult {
        when (val safety = SafetyGate.evaluate(text)) {
            is SafetyGateResult -> {
                if (safety.status == SafetyStatus.URGENT) {
                    return crisisResult(safety.message.orEmpty())
                }

                if (safety.status == SafetyStatus.NEEDS_CHECK_IN) {
                    return CriticResult(
                        hasStress = true,
                        category = "safety_check_in",
                        empathyMessage = safety.message.orEmpty(),
                        adviceList = emptyList(),
                        searchKeywords = emptyList(),
                        isReliable = true,
                        isCrisis = false
                    )
                }
            }
        }

        val analysis = PerceptionAgent.analyze(text)

        val perception = PerceptionResult(
            hasStress = analysis.hasStress,
            severity = analysis.severity,
            category = analysis.category,
            confidence = analysis.confidence,
            isCrisis = false
        )

        val ragResult = LocalRagAgent.retrieveEmpathy(perception)
        val adviceResult = AdviceGraphAgent.getAdvice(
            category = perception.category,
            history = history,
            likedTitles = emptyList()
        )

        return CriticAgent.evaluate(
            perception = perception,
            rag = ragResult,
            advice = adviceResult
        )
    }

    private fun crisisResult(message: String): CriticResult =
        CriticResult(
            hasStress = true,
            category = "crisis",
            empathyMessage = message,
            adviceList = emptyList(),
            searchKeywords = emptyList(),
            isReliable = true,
            isCrisis = true
        )
}
