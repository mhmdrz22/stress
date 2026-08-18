package com.aistudio.detected.stress.agents

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class OrchestratorAdvicePolicyTest {
    @Test
    fun `normal local response is limited by advice policy`() = runBlocking {
        val result = Orchestrator.analyze(
            text = "برای امتحان خیلی استرس دارم",
            history = emptyList(),
            deviceId = "test-device"
        )

        assertTrue(result.adviceList.size <= 3)
        assertTrue(!result.isCrisis)
    }

    @Test
    fun `crisis response remains free of ordinary advice`() = runBlocking {
        val result = Orchestrator.analyze(
            text = "می خواهم به خودم آسیب بزنم",
            history = emptyList(),
            deviceId = "test-device"
        )

        assertTrue(result.isCrisis)
        assertTrue(result.adviceList.isEmpty())
    }
}
