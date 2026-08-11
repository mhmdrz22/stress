package com.aistudio.detected.stress.viewmodel

import com.aistudio.detected.stress.data.AdviceItem
import com.aistudio.detected.stress.data.local.ChatMessage

data class StressState(
    val inputText: String = "",
    val isLoading: Boolean = false,
    val result: GeminiResponse? = null,
    val adviceList: List<AdviceItem> = emptyList(),
    val error: String? = null,
    val isOffline: Boolean = false,
    val lastInsertedMoodId: Long? = null,
    val hasSubmittedFeedback: Boolean = false,
    val chatMessages: List<ChatMessage> = emptyList(),
    val sessionId: Long = 0L
)

sealed class StressIntent {
    data class UpdateInput(val text: String) : StressIntent()
    object SubmitAnalysis : StressIntent()
    object ClearResult : StressIntent()
    data class SubmitFeedback(val isCorrect: Boolean) : StressIntent()
    data class ToggleLike(val adviceTitle: String, val isLiked: Boolean?) : StressIntent()
    object LoadSession : StressIntent()
}
