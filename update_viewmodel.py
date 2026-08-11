import sys

with open("app/src/main/java/com/aistudio/detected/stress/viewmodel/StressViewModel.kt", "r") as f:
    content = f.read()

new_viewmodel = """package com.aistudio.detected.stress.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.detected.stress.data.AdviceItem
import com.aistudio.detected.stress.data.local.AdviceFeedback
import com.aistudio.detected.stress.data.local.AppDatabase
import com.aistudio.detected.stress.data.local.MoodEntry
import com.aistudio.detected.stress.data.local.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import android.database.sqlite.SQLiteException
import kotlinx.serialization.encodeToString

@Serializable
data class GeminiResponse(
    val has_stress: Boolean,
    val category_tag: String,
    val empathy_message: String,
    val search_keywords: List<String>
)

class StressViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(StressState(sessionId = System.currentTimeMillis()))
    val state: StateFlow<StressState> = _state.asStateFlow()
    
    private val database = AppDatabase.getDatabase(application)
    private val moodDao = database.moodDao()
    private val adviceFeedbackDao = database.adviceFeedbackDao()
    private val chatDao = database.chatDao()

    val moodHistory: StateFlow<List<MoodEntry>> = moodDao.getRecentMoods()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val likedAdviceTitles: StateFlow<List<String>> = adviceFeedbackDao.getLikedAdviceTitles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadChatMessages()
    }

    private fun loadChatMessages() {
        viewModelScope.launch {
            chatDao.getMessagesForSession(_state.value.sessionId).collect { messages ->
                _state.update { it.copy(chatMessages = messages) }
            }
        }
    }

    fun processIntent(intent: StressIntent) {
        when (intent) {
            is StressIntent.UpdateInput -> {
                _state.update { it.copy(inputText = intent.text, error = null) }
            }
            is StressIntent.SubmitAnalysis -> {
                analyzeText()
            }
            is StressIntent.ClearResult -> {
                val newSessionId = System.currentTimeMillis()
                _state.update { 
                    it.copy(
                        result = null, 
                        adviceList = emptyList(), 
                        inputText = "", 
                        isOffline = false, 
                        lastInsertedMoodId = null, 
                        hasSubmittedFeedback = false,
                        sessionId = newSessionId
                    ) 
                }
                loadChatMessages()
            }
            is StressIntent.ToggleLike -> {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        adviceFeedbackDao.insertFeedback(
                            AdviceFeedback(
                                adviceTitle = intent.adviceTitle,
                                isLiked = intent.isLiked ?: false,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    } catch (e: Exception) {
                        // Handle DB insertion error
                    }
                }
            }
            is StressIntent.SubmitFeedback -> {
                val moodId = _state.value.lastInsertedMoodId
                if (moodId != null) {
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            moodDao.updateMoodCorrectness(moodId, intent.isCorrect)
                        } catch (e: Exception) {
                            // Handle DB update error
                        }
                    }
                    _state.update { it.copy(hasSubmittedFeedback = true) }
                }
            }
            is StressIntent.LoadSession -> {
                // Not implemented
            }
        }
    }

    private fun analyzeText() {
        val currentText = _state.value.inputText
        if (currentText.isBlank()) return
        
        val sessionId = _state.value.sessionId
        _state.update { it.copy(isLoading = true, error = null, inputText = "") }
        
        viewModelScope.launch(Dispatchers.Default) {
            try {
                // Add user message to DB
                withContext(Dispatchers.IO) {
                    chatDao.insertMessage(ChatMessage(sessionId = sessionId, sender = "user", content = currentText))
                }

                // Simulate processing delay for user experience
                kotlinx.coroutines.delay(1200)
                
                // Pipeline: 5-Agent Architecture
                val orchestratorResult = com.aistudio.detected.stress.agents.Orchestrator.analyze(
                    currentText, 
                    likedAdviceTitles.value.toSet()
                )
                
                // Formulate response
                val result = GeminiResponse(
                    has_stress = orchestratorResult.hasStress,
                    category_tag = orchestratorResult.category,
                    empathy_message = orchestratorResult.empathyMessage,
                    search_keywords = orchestratorResult.searchKeywords
                )
                
                // Add agent message to DB
                // Encode keywords as well to display suggestions
                val contentWithKeywords = orchestratorResult.empathyMessage + if(orchestratorResult.searchKeywords.isNotEmpty()) "|||" + orchestratorResult.searchKeywords.joinToString(",") else ""
                
                withContext(Dispatchers.IO) {
                    chatDao.insertMessage(ChatMessage(sessionId = sessionId, sender = "agent", content = contentWithKeywords))
                }

                // Save to database (respecting history for future algorithms)
                val insertedId = withContext(Dispatchers.IO) {
                    moodDao.insertMood(MoodEntry(
                        dateMillis = System.currentTimeMillis(),
                        userInput = currentText,
                        categoryTag = orchestratorResult.category,
                        hasStress = orchestratorResult.hasStress
                    ))
                }
                
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        result = result, 
                        adviceList = orchestratorResult.adviceList, 
                        isOffline = true, 
                        lastInsertedMoodId = insertedId, 
                        hasSubmittedFeedback = false
                    ) 
                }
            } catch (e: SQLiteException) {
                _state.update { it.copy(isLoading = false, error = "خطا در دسترسی به پایگاه داده. لطفاً دوباره تلاش کنید.") }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "خطای پردازش محلی: ${e.message}") }
            }
        }
    }
}
"""

with open("app/src/main/java/com/aistudio/detected/stress/viewmodel/StressViewModel.kt", "w") as f:
    f.write(new_viewmodel)
