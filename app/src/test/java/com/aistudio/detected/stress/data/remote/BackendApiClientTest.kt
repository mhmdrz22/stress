package com.aistudio.detected.stress.data.remote

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNull
import org.junit.Test

class BackendApiClientTest {

    @Test
    fun `cloud analysis is disabled without explicit consent`() = runBlocking {
        val result = BackendApiClient.analyzeChat(
            deviceId = "device-id",
            history = emptyList(),
            currentMessage = "برای امتحان استرس دارم"
        )

        assertNull(result)
    }

    @Test
    fun `cloud analysis remains disabled until secure backend exists`() = runBlocking {
        val result = NetworkClient.analyzeViaBackend(
            deviceId = "device-id",
            history = emptyList(),
            currentMessage = "حالم خوب نیست",
            cloudConsentGranted = true
        )

        assertNull(result)
    }
}
