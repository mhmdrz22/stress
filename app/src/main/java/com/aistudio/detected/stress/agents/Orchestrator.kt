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
        val baseAdvice = AdviceGraphAgent.getAdvice(
            category = perception.category,
            history = history,
            likedTitles = emptyList()
        )

        val shownTitles = history
            .flatMap { message ->
                baseAdvice.adviceList
                    .filter { advice -> message.content.contains(advice.title) }
                    .map { advice -> advice.title }
            }
            .toSet()

        val policyAdvice = AdvicePolicyAgent.select(
            AdvicePolicyRequest(
                category = perception.category,
                stressLevel = null,
                isCrisis = false,
                previouslyShownTitles = shownTitles,
                likedTitles = emptySet()
            )
        )

        return CriticAgent.evaluate(
            perception = perception,
            rag = empathy,
            advice = AdviceGraphResult(
                adviceList = policyAdvice.adviceList,
                searchKeywords = policyAdvice.searchKeywords
            )
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
