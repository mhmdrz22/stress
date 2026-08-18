package com.aistudio.detected.stress.agents

import org.junit.Assert.assertEquals
import org.junit.Test

class SafetyGateTest {
    @Test
    fun `explicit Persian self harm intent is urgent`() {
        val result = SafetyGate.evaluate("می‌خوام به خودم آسیب بزنم")

        assertEquals(SafetyStatus.URGENT, result.status)
    }

    @Test
    fun `explicit English self harm intent is urgent`() {
        val result = SafetyGate.evaluate("I want to kill myself")

        assertEquals(SafetyStatus.URGENT, result.status)
    }

    @Test
    fun `ambiguous hopelessness needs check in`() {
        val result = SafetyGate.evaluate("کاش نبودم")

        assertEquals(SafetyStatus.NEEDS_CHECK_IN, result.status)
    }

    @Test
    fun `ordinary mention of death does not automatically become urgent`() {
        val result = SafetyGate.evaluate("امروز درباره مرگ در یک فیلم صحبت می‌کردیم")

        assertEquals(SafetyStatus.CLEAR, result.status)
    }

    @Test
    fun `ordinary stress remains clear`() {
        val result = SafetyGate.evaluate("برای امتحان فردا خیلی استرس دارم")

        assertEquals(SafetyStatus.CLEAR, result.status)
    }
}
