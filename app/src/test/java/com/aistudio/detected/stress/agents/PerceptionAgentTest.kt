package com.aistudio.detected.stress.agents

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PerceptionAgentTest {
    @Test
    fun `exam stress is categorised locally`() {
        val result = PerceptionAgent.analyze("برای امتحان فردا خیلی استرس و نگرانی دارم")

        assertTrue(result.hasStress)
        assertEquals("exam_stress", result.category)
        assertFalse(result.isCrisis)
    }

    @Test
    fun `sleep concern is categorised locally`() {
        val result = PerceptionAgent.analyze("چند شب است بیخوابی دارم و خیلی خسته ام")

        assertTrue(result.hasStress)
        assertEquals("sleep", result.category)
        assertFalse(result.isCrisis)
    }

    @Test
    fun `ordinary text remains general`() {
        val result = PerceptionAgent.analyze("امروز هوا خوب است")

        assertFalse(result.hasStress)
        assertEquals("general", result.category)
        assertFalse(result.isCrisis)
    }

    @Test
    fun `crisis-like text is not decided here`() {
        val result = PerceptionAgent.analyze("کاش نبودم")

        assertFalse(result.isCrisis)
    }
}
