package com.aistudio.detected.stress.viewmodel

import com.aistudio.detected.stress.data.StressLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StressAssessmentViewModelTest {
    @Test
    fun `submit requires all answers`() {
        val viewModel = StressAssessmentViewModel(questionCount = 3)
        viewModel.setAnswer(0, 1)

        viewModel.submit()

        assertNull(viewModel.uiState.result)
        assertTrue(viewModel.uiState.validationMessage != null)
    }

    @Test
    fun `submit exposes urgent result when safety concern is selected`() {
        val viewModel = StressAssessmentViewModel(questionCount = 2)
        viewModel.setAnswer(0, 0)
        viewModel.setAnswer(1, 0)
        viewModel.setImmediateSafetyConcern(true)

        viewModel.submit()

        assertEquals(StressLevel.URGENT, viewModel.uiState.result?.level)
    }

    @Test
    fun `changing an answer clears an old result`() {
        val viewModel = StressAssessmentViewModel(questionCount = 1)
        viewModel.setAnswer(0, 4)
        viewModel.submit()
        viewModel.setAnswer(0, 0)

        assertNull(viewModel.uiState.result)
    }
}
