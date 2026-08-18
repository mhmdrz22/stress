package com.aistudio.detected.stress.viewmodel

import androidx.lifecycle.ViewModel
import com.aistudio.detected.stress.data.StressAssessmentEngine
import com.aistudio.detected.stress.data.StressAssessmentInput
import com.aistudio.detected.stress.data.StressAssessmentResult

data class StressAssessmentUiState(
    val answers: List<Int?>,
    val hasImmediateSafetyConcern: Boolean = false,
    val result: StressAssessmentResult? = null,
    val validationMessage: String? = null
) {
    val isComplete: Boolean
        get() = answers.all { it != null }
}

class StressAssessmentViewModel(
    questionCount: Int
) : ViewModel() {
    init {
        require(questionCount > 0) { "questionCount must be greater than zero." }
    }

    private val initialAnswers = List(questionCount) { null as Int? }

    var uiState: StressAssessmentUiState = StressAssessmentUiState(answers = initialAnswers)
        private set

    fun setAnswer(index: Int, answer: Int) {
        require(index in uiState.answers.indices) { "Invalid question index." }
        require(answer in StressAssessmentEngine.MIN_ANSWER..StressAssessmentEngine.MAX_ANSWER) {
            "Answer must be between ${StressAssessmentEngine.MIN_ANSWER} and ${StressAssessmentEngine.MAX_ANSWER}."
        }

        uiState = uiState.copy(
            answers = uiState.answers.mapIndexed { currentIndex, currentAnswer ->
                if (currentIndex == index) answer else currentAnswer
            },
            result = null,
            validationMessage = null
        )
    }

    fun setImmediateSafetyConcern(value: Boolean) {
        uiState = uiState.copy(
            hasImmediateSafetyConcern = value,
            result = null,
            validationMessage = null
        )
    }

    fun submit() {
        val completedAnswers = uiState.answers
        if (completedAnswers.any { it == null }) {
            uiState = uiState.copy(
                result = null,
                validationMessage = "Please answer every question before viewing your result."
            )
            return
        }

        uiState = uiState.copy(
            result = StressAssessmentEngine.assess(
                StressAssessmentInput(
                    answers = completedAnswers.filterNotNull(),
                    hasImmediateSafetyConcern = uiState.hasImmediateSafetyConcern
                )
            ),
            validationMessage = null
        )
    }

    fun restart() {
        uiState = StressAssessmentUiState(answers = initialAnswers)
    }
}
