package com.aistudio.detected.stress.agents

import com.aistudio.detected.stress.data.StressLevel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OrchestratorAssessmentTest {

    @Test
    fun `high assessment level limits normal advice to two items`() = runBlocking {
        val result = Orchestrator.analyze(
            text = "برای امتحان استرس دارم",
            history = emptyList(),
            deviceId = "test-device",
            assessmentLevel = StressLevel.HIGH
        )

        assertTrue(!result.isCrisis)
        assertTrue(result.adviceList.size <= 2)
    }

    @Test
    fun `urgent assessment level bypasses normal pipeline`() = runBlocking {
        val result = Orchestrator.analyze(
            text = "حالم خوب نیست",
            history = emptyList(),
            deviceId = "test-device",
            assessmentLevel = StressLevel.URGENT
        )

        assertTrue(result.isCrisis)
        assertEquals("crisis", result.category)
        assertTrue(result.adviceList.isEmpty())
        assertTrue(result.searchKeywords.isEmpty())
    }
}
