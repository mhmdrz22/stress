package com.aistudio.detected.stress.viewmodel

import com.aistudio.detected.stress.data.StressAssessmentResult
import com.aistudio.detected.stress.data.StressLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ChatAssessmentStateTest {

    private fun moderateAssessment() = StressAssessmentResult(
        totalScore = 8,
        maxScore = 16,
        level = StressLevel.MODERATE,
        assessmentVersion = "1.0",
        disclaimer = "این ارزیابی تشخیص پزشکی نیست.",
        recommendedActions = emptyList(),
        shouldEscalate = false
    )

    @Test
    fun `assessment completion stores result in chat state`() {
        val state = ChatState()

        val updatedState = state.copy(
            assessmentResult = moderateAssessment()
        )

        assertEquals(
            StressLevel.MODERATE,
            updatedState.assessmentResult?.level
        )
    }

    @Test
    fun `clearing session removes previous assessment`() {
        val stateWithAssessment = ChatState(
            assessmentResult = moderateAssessment()
        )

        val clearedState = stateWithAssessment.copy(
            assessmentResult = null,
            sessionId = 1234L
        )

        assertNull(clearedState.assessmentResult)
    }
}
