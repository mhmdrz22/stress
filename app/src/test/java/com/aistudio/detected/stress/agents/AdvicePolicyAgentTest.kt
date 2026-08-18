package com.aistudio.detected.stress.agents

import com.aistudio.detected.stress.data.StressLevel
import org.junit.Assert.assertTrue
import org.junit.Test

class AdvicePolicyAgentTest {
    @Test
    fun `crisis never returns ordinary advice`() {
        val result = AdvicePolicyAgent.select(
            AdvicePolicyRequest(
                category = "anxiety",
                stressLevel = StressLevel.URGENT,
                isCrisis = true
            )
        )

        assertTrue(result.adviceList.isEmpty())
        assertTrue(result.searchKeywords.isEmpty())
    }

    @Test
    fun `high stress limits advice to two items`() {
        val result = AdvicePolicyAgent.select(
            AdvicePolicyRequest(
                category = "anxiety",
                stressLevel = StressLevel.HIGH,
                isCrisis = false
            )
        )

        assertTrue(result.adviceList.size <= 2)
    }

    @Test
    fun `previously shown advice is avoided when alternatives exist`() {
        val first = AdvicePolicyAgent.select(
            AdvicePolicyRequest(
                category = "anxiety",
                stressLevel = StressLevel.MODERATE,
                isCrisis = false
            )
        )
        val shown = first.adviceList.map { it.id }.toSet()

        val second = AdvicePolicyAgent.select(
            AdvicePolicyRequest(
                category = "anxiety",
                stressLevel = StressLevel.MODERATE,
                isCrisis = false,
                previouslyShownIds = shown
            )
        )

        assertTrue(second.adviceList.none { it.id in shown } || second.adviceList.isEmpty())
    }
}
