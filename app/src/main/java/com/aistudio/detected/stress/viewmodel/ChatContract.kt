package com.aistudio.detected.stress.viewmodel

import com.aistudio.detected.stress.data.AdviceItem
import com.aistudio.detected.stress.data.StressAssessmentResult
import com.aistudio.detected.stress.data.local.ChatMessage

data class ChatState(
    val inputText: String = "",
    val isLoading: Boolean = false,
    val result: GeminiResponse? = null,
    val adviceList: List<AdviceItem> = emptyList(),
    val error: String? = null,
    val isOffline: Boolean = false,
    val lastInsertedMoodId: Long? = null,
    val hasSubmittedFeedback: Boolean = false,
    val chatMessages: List<ChatMessage> = emptyList(),
    val sessionId: Long = 0L,
    val assessmentResult: StressAssessmentResult? = null
)

sealed class ChatIntent {
    data class UpdateInput(val text: String) : ChatIntent()
    object SubmitAnalysis : ChatIntent()
    object ClearResult : ChatIntent()
    data class SubmitFeedback(val isCorrect: Boolean) : ChatIntent()
    data class ToggleLike(val adviceTitle: String, val isLiked: Boolean?) : ChatIntent()
    object LoadSession : ChatIntent()
    data class AssessmentCompleted(val result: StressAssessmentResult) : ChatIntent()
    object ClearAssessment : ChatIntent()
}
