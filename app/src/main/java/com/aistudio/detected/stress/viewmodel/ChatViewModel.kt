package com.aistudio.detected.stress.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.detected.stress.data.AdviceItem
import com.aistudio.detected.stress.data.local.AdviceFeedback
import com.aistudio.detected.stress.data.local.AppDatabase
import com.aistudio.detected.stress.data.local.MoodEntry
import com.aistudio.detected.stress.data.local.ChatMessage
import com.aistudio.detected.stress.data.local.StressAssessmentEntry
import com.aistudio.detected.stress.data.StressAssessmentResult
import com.aistudio.detected.stress.data.StressLevel
import com.aistudio.detected.stress.agents.SafetyGate
import com.aistudio.detected.stress.agents.SafetyStatus
import com.aistudio.detected.stress.data.local.PrivacyPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import android.database.sqlite.SQLiteException
import java.util.UUID

@Serializable
data class GeminiResponse(
    val has_stress: Boolean,
    val category_tag: String,
    val empathy_message: String,
    val search_keywords: List<String>
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(ChatState(sessionId = System.currentTimeMillis()))
    val state: StateFlow<ChatState> = _state.asStateFlow()
    
    private val database = AppDatabase.getDatabase(application)
    private val moodDao = database.moodDao()
    private val adviceFeedbackDao = database.adviceFeedbackDao()
    private val chatDao = database.chatDao()
    private val stressAssessmentDao = database.stressAssessmentDao()
    private val privacyPreferences = PrivacyPreferences(application)
    
    private var deviceId: String = UUID.randomUUID().toString()
    
    val moodHistory: StateFlow<List<MoodEntry>> = moodDao.getRecentMoods()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val allChatMessages: StateFlow<List<ChatMessage>> = chatDao.getAllMessagesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val likedAdviceIds: StateFlow<List<String>> = adviceFeedbackDao.getLikedAdviceIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val assessmentHistory = stressAssessmentDao.observeAll()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    init {
        _state.update {
            it.copy(
                isChatHistoryEnabled = privacyPreferences.isChatHistoryEnabled()
            )
        }
        loadChatMessages()
    }

    private fun loadChatMessages() {
        viewModelScope.launch {
            chatDao.getMessagesForSession(_state.value.sessionId).collect { messages ->
                _state.update { it.copy(chatMessages = messages) }
            }
        }
    }

    fun processIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.UpdateInput -> {
                _state.update { it.copy(inputText = intent.text, error = null) }
            }
            is ChatIntent.SubmitAnalysis -> {
                analyzeText()
            }
            is ChatIntent.ClearResult -> {
                val newSessionId = System.currentTimeMillis()
                _state.update { 
                    it.copy(
                        result = null, 
                        adviceList = emptyList(), 
                        inputText = "", 
                        isOffline = false, 
                        lastInsertedMoodId = null, 
                        hasSubmittedFeedback = false,
                        sessionId = newSessionId,
                        assessmentResult = null
                    ) 
                }
                loadChatMessages()
            }
            is ChatIntent.ToggleLike -> {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        adviceFeedbackDao.insertFeedback(
                            AdviceFeedback(
                                adviceId = intent.adviceId,
                                isLiked = intent.isLiked ?: false,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    } catch (e: Exception) {
                        // Handle DB insertion error
                    }
                }
            }
            is ChatIntent.SubmitFeedback -> {
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
            is ChatIntent.LoadSession -> {
                // Not implemented
            }
            is ChatIntent.AssessmentCompleted -> {
                _state.update {
                    it.copy(
                        assessmentResult = intent.result,
                        error = null
                    )
                }
                saveAssessment(intent.result)
            }
            is ChatIntent.ClearAssessment -> {
                _state.update {
                    it.copy(assessmentResult = null)
                }
            }
            is ChatIntent.ClearAssessmentHistory -> {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        stressAssessmentDao.deleteAll()
                    } catch (_: Exception) {
                        _state.update {
                            it.copy(
                                error = "حذف تاریخچهٔ ارزیابی انجام نشد. دوباره تلاش کن."
                            )
                        }
                    }
                }
            }
            is ChatIntent.SetChatHistoryEnabled -> {
                privacyPreferences.setChatHistoryEnabled(intent.enabled)
                _state.update {
                    it.copy(isChatHistoryEnabled = intent.enabled)
                }
                if (!intent.enabled) {
                    viewModelScope.launch(Dispatchers.IO) {
                        chatDao.clearHistory()
                    }
                }
            }
            is ChatIntent.ClearChatHistory -> {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        chatDao.clearHistory()
                        _state.update { it.copy(chatMessages = emptyList()) }
                    } catch (_: Exception) {
                        _state.update {
                            it.copy(
                                error = "حذف تاریخچهٔ گفتگو انجام نشد. دوباره تلاش کن."
                            )
                        }
                    }
                }
            }
        }
    }

    private fun saveAssessment(result: StressAssessmentResult) {
        /*
         * بحران یا ارزیابی URGENT ذخیره نمیشود.
         * متن پاسخها هم هیچوقت وارد دیتابیس نمیشود.
         */
        if (result.level == StressLevel.URGENT) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                stressAssessmentDao.insert(
                    StressAssessmentEntry(
                        completedAtEpochMillis = System.currentTimeMillis(),
                        totalScore = result.totalScore,
                        maxScore = result.maxScore,
                        level = result.level.name,
                        assessmentVersion = result.assessmentVersion
                    )
                )
            } catch (_: Exception) {
                // شکست ذخیره‌سازی نباید مانع نمایش نتیجه به کاربر شود.
            }
        }
    }

    private fun analyzeText() {
        val currentText = _state.value.inputText.trim()
        if (currentText.isBlank()) return
        
        val sessionId = _state.value.sessionId
        val chatHistory = _state.value.chatMessages.toList()
        
        _state.update { it.copy(isLoading = true, error = null, inputText = "") }
        
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val safetyStatus = SafetyGate.evaluate(currentText).status
                val mayPersistChat = _state.value.isChatHistoryEnabled &&
                    safetyStatus == SafetyStatus.CLEAR

                val safety = SafetyGate.evaluate(currentText)

                if (safety.status == SafetyStatus.URGENT) {
                    val crisisUserMsg = ChatMessage(sessionId = sessionId, sender = "user", content = currentText)
                    val crisisAgentMsg = ChatMessage(sessionId = sessionId, sender = "agent", content = "${safety.message.orEmpty()}|||crisis|||")
            
                    _state.update {
                        it.copy(
                            isLoading = false,
                            chatMessages = it.chatMessages + crisisUserMsg + crisisAgentMsg,
                            error = null
                        )
                    }
                    // عمداً متن بحران ذخیره یا به شبکه فرستاده نمیشود.
                    return@launch
                }
                
                // Add user message to DB
                if (mayPersistChat) {
                    withContext(Dispatchers.IO) {
                        chatDao.insertMessage(ChatMessage(sessionId = sessionId, sender = "user", content = currentText))
                    }
                }
                
                // Pipeline: Multi-Agent Architecture with Cloud + Local Fallback
                val assessmentLevel = _state.value.assessmentResult?.level
                
                val likedIds = withContext(Dispatchers.IO) {
                    adviceFeedbackDao.getLikedAdviceIds().first()
                }
                
                val orchestratorResult = com.aistudio.detected.stress.agents.Orchestrator.analyze(
                    text = currentText, 
                    history = chatHistory,
                    deviceId = deviceId,
                    assessmentLevel = assessmentLevel,
                    likedIds = likedIds.toSet()
                )
                
                // Formulate response
                val result = GeminiResponse(
                    has_stress = orchestratorResult.hasStress,
                    category_tag = orchestratorResult.category,
                    empathy_message = orchestratorResult.empathyMessage,
                    search_keywords = orchestratorResult.searchKeywords
                )
                
                // Encode category and direct video links (Title~Url)
                val videoLinks = orchestratorResult.adviceList.filter { it.videoUrl != null }.map { "${it.title}~${it.videoUrl}" }
                // Fallback to regular keywords if no videos are found
                val finalKeywords = if (videoLinks.isNotEmpty()) videoLinks else orchestratorResult.searchKeywords
                val keywordStr = if(finalKeywords.isNotEmpty()) finalKeywords.joinToString(",") else ""
                val adviceIdsStr = orchestratorResult.adviceList.joinToString(",") { it.id }
                val contentWithKeywords = "${orchestratorResult.empathyMessage}|||${orchestratorResult.category}|||$keywordStr|||$adviceIdsStr"
                
                if (mayPersistChat && !orchestratorResult.isCrisis) {
                    withContext(Dispatchers.IO) {
                        chatDao.insertMessage(ChatMessage(sessionId = sessionId, sender = "agent", content = contentWithKeywords))
                    }
                }

                val insertedId = withContext(Dispatchers.IO) {
                    moodDao.insertMood(MoodEntry(
                        dateMillis = System.currentTimeMillis(),
                        categoryTag = orchestratorResult.category,
                        hasStress = orchestratorResult.hasStress
                    ))
                }
                
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        result = result, 
                        adviceList = orchestratorResult.adviceList, 
                        isOffline = false, 
                        lastInsertedMoodId = insertedId, 
                        hasSubmittedFeedback = false
                    ) 
                }
            } catch (e: SQLiteException) {
                _state.update { it.copy(isLoading = false, error = "خطا در دسترسی به پایگاه داده. لطفاً دوباره تلاش کنید.") }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "خطای پردازش: ${e.message}") }
            }
        }
    }
}
