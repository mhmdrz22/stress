package com.aistudio.detected.stress.agents

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatPersistencePolicyTest {

    private fun mayPersist(
        historyEnabled: Boolean,
        text: String
    ): Boolean {
        return historyEnabled &&
            SafetyGate.evaluate(text).status == SafetyStatus.CLEAR
    }

    @Test
    fun `ordinary chat can be stored only after consent`() {
        assertFalse(
            mayPersist(
                historyEnabled = false,
                text = "برای امتحان فردا استرس دارم"
            )
        )

        assertTrue(
            mayPersist(
                historyEnabled = true,
                text = "برای امتحان فردا استرس دارم"
            )
        )
    }

    @Test
    fun `urgent message is never stored even with consent`() {
        assertFalse(
            mayPersist(
                historyEnabled = true,
                text = "می خواهم به خودم آسیب بزنم"
            )
        )
    }

    @Test
    fun `ambiguous safety check in is never stored even with consent`() {
        assertFalse(
            mayPersist(
                historyEnabled = true,
                text = "کاش نبودم"
            )
        )
    }
}
