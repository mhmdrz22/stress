package com.aistudio.detected.stress.agents

import com.aistudio.detected.stress.data.local.ChatMessage

/**
 * Coordinates the local support pipeline.
 * Safety is evaluated first and can short-circuit every other agent.
 */
object Orchestrator {

    suspend fun analyze(
        text: String,
        history: List<ChatMessage>,
        deviceId: String
    ): CriticResult {
        when (val safety = SafetyGate.evaluate(text)) {
            is SafetyGateResult -> when (safety.status) {
                SafetyStatus.URGENT -> return crisisResult(safety.userFacingMessage.orEmpty())
                SafetyStatus.NEEDS_CHECK_IN -> return checkInResult(safety.userFacingMessage.orEmpty())
                SafetyStatus.CLEAR -> Unit
            }
        }

        // The normal flow is fully local. Do not send chat content to a remote model here.
        val analysis = PerceptionAgent.analyze(text)
        val perception = PerceptionResult(
            hasStress = analysis.hasStress,
            severity = analysis.severity,
            category = analysis.category,
            confidence = analysis.confidence,
            isCrisis = false
        )

        val empathy = LocalRagAgent.retrieveEmpathy(perception)
        val advice = AdviceGraphAgent.getAdvice(
            category = perception.category,
            history = history,
            likedTitles = emptyList()
        )

        return CriticAgent.evaluate(
            perception = perception,
            rag = empathy,
            advice = advice
        )
    }

    private fun crisisResult(message: String): CriticResult = CriticResult(
        hasStress = true,
        category = "crisis",
        empathyMessage = message,
        adviceList = emptyList(),
        searchKeywords = emptyList(),
        isReliable = true,
        isCrisis = true
    )

    private fun checkInResult(message: String): CriticResult = CriticResult(
        hasStress = true,
        category = "safety_check_in",
        empathyMessage = message,
        adviceList = emptyList(),
        searchKeywords = emptyList(),
        isReliable = true,
        isCrisis = false
    )
}
