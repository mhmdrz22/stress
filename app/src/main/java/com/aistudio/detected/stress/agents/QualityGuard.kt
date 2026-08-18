package com.aistudio.detected.stress.agents

import com.aistudio.detected.stress.data.AdviceItem
import com.aistudio.detected.stress.data.StressLevel

data class AssistantDraft(
    val message: String,
    val advice: List<AdviceItem>,
    val keywords: List<String>,
    val category: String,
    val confidence: Float,
    val stressLevel: StressLevel? = null,
    val isCrisis: Boolean = false
)

data class GuardDecision(
    val message: String,
    val advice: List<AdviceItem>,
    val keywords: List<String>,
    val blockedReasons: List<String>
)

object QualityGuard {

    private val diagnosticPatterns = listOf(
        Regex("""تو افسردگی داری"""),
        Regex("""شما افسردگی دارید"""),
        Regex("""تو اختلال"""),
        Regex("""شما اختلال"""),
        Regex("""\byou have depression\b""", RegexOption.IGNORE_CASE),
        Regex("""\byou have an? disorder\b""", RegexOption.IGNORE_CASE)
    )

    private val guaranteePatterns = listOf(
        Regex("""حتماً خوب می[ ]?شی"""),
        Regex("""قطعاً درمان می[ ]?شی"""),
        Regex("""تضمینی"""),
        Regex("""\bwill definitely cure\b""", RegexOption.IGNORE_CASE),
        Regex("""\bguaranteed cure\b""", RegexOption.IGNORE_CASE)
    )

    private val medicationPatterns = listOf(
        Regex("""دارو(ی|ها|یت)?\s+(بخور|مصرف کن|قطع کن|عوض کن)"""),
        Regex("""قرص\s+(بخور|مصرف کن|قطع کن|عوض کن)"""),
        Regex("""دوز\s+دارو"""),
        Regex("""\b(start|stop|change|take)\s+(your\s+)?medication\b""", RegexOption.IGNORE_CASE),
        Regex("""\bchange\s+your\s+dose\b""", RegexOption.IGNORE_CASE)
    )

    fun validate(draft: AssistantDraft): GuardDecision {
        if (draft.isCrisis || draft.stressLevel == StressLevel.URGENT) {
            return GuardDecision(
                message = SafetyGate.urgentMessage(),
                advice = emptyList(),
                keywords = emptyList(),
                blockedReasons = listOf("crisis_path")
            )
        }

        val blockedReasons = mutableListOf<String>()
        var message = draft.message.trim()

        if (containsAny(message, diagnosticPatterns)) {
            message = message.replace(
                Regex("""تو افسردگی داری|شما افسردگی دارید"""),
                "ممکن است حال روحی سختی را تجربه کنی"
            )
            message = message.replace(
                Regex("""تو اختلال|شما اختلال"""),
                "ممکن است با یک چالش"
            )
            blockedReasons += "diagnostic_language"
        }

        if (containsAny(message, guaranteePatterns)) {
            message = guaranteePatterns.fold(message) { current, pattern ->
                current.replace(pattern, "ممکن است کمککننده باشد")
            }
            blockedReasons += "guarantee_language"
        }

        if (containsAny(message, medicationPatterns)) {
            message = message.replace(
                medicationPatterns.firstOrNull { it.containsMatchIn(message) }
                    ?: Regex("$^"),
                "برای پرسشهای مربوط به دارو، با پزشک یا داروساز مشورت کن."
            )
            blockedReasons += "medication_instruction"
        }

        if (draft.confidence < 0.5f && message.isNotBlank()) {
            message = "ممکن است برداشت من کامل نباشد. $message"
            blockedReasons += "low_confidence_prefix"
        }

        return GuardDecision(
            message = message.ifBlank {
                "من اینجا هستم تا به حرفت گوش بدهم. دوست داری بیشتر بگویی چه چیزی درگیرت کرده؟"
            },
            advice = draft.advice,
            keywords = draft.keywords,
            blockedReasons = blockedReasons
        )
    }

    private fun containsAny(
        text: String,
        patterns: List<Regex>
    ): Boolean = patterns.any { it.containsMatchIn(text) }
}
