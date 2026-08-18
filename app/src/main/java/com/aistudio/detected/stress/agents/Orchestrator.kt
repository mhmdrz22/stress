package com.aistudio.detected.stress.agents

import com.aistudio.detected.stress.data.StressLevel
import com.aistudio.detected.stress.data.local.ChatMessage

/**
 * Coordinates the local support pipeline.
 * Safety is evaluated first and can short-circuit every other agent.
 */
object Orchestrator {

    suspend fun analyze(
        text: String,
        history: List<ChatMessage>,
        deviceId: String,
        assessmentLevel: StressLevel? = null
    ): CriticResult {
        when (val safety = SafetyGate.evaluate(text)) {
            is SafetyGateResult -> when (safety.status) {
                SafetyStatus.URGENT -> return crisisResult(
                    safety.message.orEmpty()
                )

                SafetyStatus.NEEDS_CHECK_IN -> return checkInResult(
                    safety.message.orEmpty()
                )

                SafetyStatus.CLEAR -> Unit
            }
        }

        // نتیجهٔ فوری پرسشنامه بر مسیر معمول اولویت دارد.
        if (assessmentLevel == StressLevel.URGENT) {
            return crisisResult(SafetyGate.urgentMessage())
        }

        val analysis = PerceptionAgent.analyze(text)

        val perception = PerceptionResult(
            hasStress = analysis.hasStress ||
                assessmentLevel == StressLevel.MODERATE ||
                assessmentLevel == StressLevel.HIGH,
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
                stressLevel = assessmentLevel,
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

    private fun checkInResult(message: String): CriticResult =
        CriticResult(
            hasStress = true,
            category = "safety_check_in",
            empathyMessage = message,
            adviceList = emptyList(),
            searchKeywords = emptyList(),
            isReliable = true,
            isCrisis = false
        )
}
