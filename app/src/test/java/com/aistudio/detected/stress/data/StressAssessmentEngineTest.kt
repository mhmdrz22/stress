package com.aistudio.detected.stress.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StressAssessmentEngineTest {
    @Test
    fun `low score returns low level`() {
        val result = StressAssessmentEngine.assess(StressAssessmentInput(answers = listOf(0, 1, 1, 0)))

        assertEquals(StressLevel.LOW, result.level)
        assertEquals(2, result.totalScore)
    }

    @Test
    fun `mid score returns moderate level`() {
        val result = StressAssessmentEngine.assess(StressAssessmentInput(answers = listOf(2, 2, 2, 2)))

        assertEquals(StressLevel.MODERATE, result.level)
    }

    @Test
    fun `high score returns high level and escalation`() {
        val result = StressAssessmentEngine.assess(StressAssessmentInput(answers = listOf(4, 4, 3, 3)))

        assertEquals(StressLevel.HIGH, result.level)
        assertTrue(result.shouldEscalate)
    }

    @Test
    fun `immediate safety concern overrides score`() {
        val result = StressAssessmentEngine.assess(
            StressAssessmentInput(answers = listOf(0, 0, 0, 0), hasImmediateSafetyConcern = true)
        )

        assertEquals(StressLevel.URGENT, result.level)
        assertTrue(result.shouldEscalate)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `out of range answer is rejected`() {
        StressAssessmentEngine.assess(StressAssessmentInput(answers = listOf(5)))
    }
}
