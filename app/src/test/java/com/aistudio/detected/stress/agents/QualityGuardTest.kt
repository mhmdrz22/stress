package com.aistudio.detected.stress.agents

import com.aistudio.detected.stress.data.StressLevel
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QualityGuardTest {

    private fun draft(
        message: String,
        confidence: Float = 0.9f,
        isCrisis: Boolean = false,
        level: StressLevel? = null
    ) = AssistantDraft(
        message = message,
        advice = emptyList(),
        keywords = emptyList(),
        category = "anxiety",
        confidence = confidence,
        stressLevel = level,
        isCrisis = isCrisis
    )

    @Test
    fun `crisis blocks ordinary content`() {
        val result = QualityGuard.validate(
            draft(
                message = "یک تمرین تنفس انجام بده",
                isCrisis = true,
                level = StressLevel.URGENT
            )
        )

        assertTrue(result.advice.isEmpty())
        assertTrue(result.keywords.isEmpty())
        assertTrue(result.blockedReasons.contains("crisis_path"))
    }

    @Test
    fun `diagnostic language is softened`() {
        val result = QualityGuard.validate(
            draft("تو افسردگی داری و باید درمان شوی")
        )

        assertFalse(result.message.contains("تو افسردگی داری"))
        assertTrue(result.blockedReasons.contains("diagnostic_language"))
    }

    @Test
    fun `guaranteed outcome is removed`() {
        val result = QualityGuard.validate(
            draft("این تمرین حتماً خوب میشی")
        )

        assertFalse(result.message.contains("حتماً خوب میشی"))
        assertTrue(result.blockedReasons.contains("guarantee_language"))
    }

    @Test
    fun `low confidence gets cautious prefix`() {
        val result = QualityGuard.validate(
            draft(
                message = "به نظر میرسد تحت فشار هستی.",
                confidence = 0.2f
            )
        )

        assertTrue(
            result.message.startsWith("ممکن است برداشت من کامل نباشد.")
        )
    }
}
