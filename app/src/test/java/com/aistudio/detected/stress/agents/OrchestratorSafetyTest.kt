package com.aistudio.detected.stress.agents

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OrchestratorSafetyTest {
    @Test
    fun `urgent message bypasses normal advice`() = runBlocking {
        val result = Orchestrator.analyze(
            text = "می‌خوام به خودم آسیب بزنم",
            history = emptyList(),
            deviceId = "test-device"
        )

        assertTrue(result.isCrisis)
        assertEquals("crisis", result.category)
        assertTrue(result.adviceList.isEmpty())
        assertTrue(result.searchKeywords.isEmpty())
    }

    @Test
    fun `ambiguous message asks a safety question without normal advice`() = runBlocking {
        val result = Orchestrator.analyze(
            text = "کاش نبودم",
            history = emptyList(),
            deviceId = "test-device"
        )

        assertEquals("safety_check_in", result.category)
        assertTrue(result.adviceList.isEmpty())
        assertTrue(result.searchKeywords.isEmpty())
    }
}
