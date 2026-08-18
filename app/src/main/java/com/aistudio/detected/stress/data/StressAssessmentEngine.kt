package com.aistudio.detected.stress.data

/**
 * A non-diagnostic, deterministic self-report stress check-in.
 * Keep crisis handling outside of generative AI: urgent signals always win.
 */
enum class StressLevel { LOW, MODERATE, HIGH, URGENT }

data class StressAssessmentInput(
    val answers: List<Int>,
    val hasImmediateSafetyConcern: Boolean = false
)

data class StressAssessmentResult(
    val totalScore: Int,
    val maxScore: Int,
    val level: StressLevel,
    val assessmentVersion: String,
    val disclaimer: String,
    val recommendedActions: List<String>,
    val shouldEscalate: Boolean
)

object StressAssessmentEngine {
    const val ASSESSMENT_VERSION = "1.0"
    const val MIN_ANSWER = 0
    const val MAX_ANSWER = 4

    fun assess(input: StressAssessmentInput): StressAssessmentResult {
        require(input.answers.isNotEmpty()) { "At least one answer is required." }
        require(input.answers.all { it in MIN_ANSWER..MAX_ANSWER }) {
            "Answers must be between $MIN_ANSWER and $MAX_ANSWER."
        }

        val total = input.answers.sum()
        val max = input.answers.size * MAX_ANSWER
        val level = if (input.hasImmediateSafetyConcern) {
            StressLevel.URGENT
        } else {
            levelFor(total, max)
        }

        return StressAssessmentResult(
            totalScore = total,
            maxScore = max,
            level = level,
            assessmentVersion = ASSESSMENT_VERSION,
            disclaimer = "This check-in is not a diagnosis or a substitute for professional care.",
            recommendedActions = actionsFor(level),
            shouldEscalate = level == StressLevel.HIGH || level == StressLevel.URGENT
        )
    }

    private fun levelFor(total: Int, max: Int): StressLevel {
        val ratio = total.toDouble() / max
        return when {
            ratio < 0.34 -> StressLevel.LOW
            ratio < 0.67 -> StressLevel.MODERATE
            else -> StressLevel.HIGH
        }
    }

    private fun actionsFor(level: StressLevel): List<String> = when (level) {
        StressLevel.LOW -> listOf(
            "Take a short pause and notice your breathing.",
            "Support your routine with sleep, hydration, and gentle movement."
        )
        StressLevel.MODERATE -> listOf(
            "Try a brief grounding or paced-breathing exercise now.",
            "Choose one manageable task and take a short break before the next one.",
            "Consider speaking with a health professional if this persists or affects daily life."
        )
        StressLevel.HIGH -> listOf(
            "Pause and use a short grounding exercise in a safe place.",
            "Reach out to someone you trust today.",
            "Consider contacting a licensed health professional, especially if symptoms persist or disrupt daily life."
        )
        StressLevel.URGENT -> listOf(
            "If you may be in immediate danger or cannot stay safe, contact local emergency services now.",
            "Reach a trusted person nearby and do not stay alone if possible.",
            "Use a local crisis service where one is available."
        )
    }
}
