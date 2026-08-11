package com.aistudio.detected.stress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.detected.stress.BuildConfig
import com.aistudio.detected.stress.data.AdviceItem
import com.aistudio.detected.stress.data.LocalAdviceGraph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GeminiResponse(
    val has_stress: Boolean,
    val category_tag: String,
    val empathy_message: String,
    val search_keywords: List<String>
)

sealed class AppState {
    object Idle : AppState()
    object Loading : AppState()
    data class Success(val result: GeminiResponse, val adviceList: List<AdviceItem>) : AppState()
    data class Error(val message: String) : AppState()
}

class StressViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<AppState>(AppState.Idle)
    val uiState: StateFlow<AppState> = _uiState
    
    private val jsonParser = Json { ignoreUnknownKeys = true }

    fun analyze(text: String) {
        if (text.isBlank()) return
        _uiState.value = AppState.Loading
        
        viewModelScope.launch {
            try {
                // Simulate processing delay for user experience
                kotlinx.coroutines.delay(800)
                
                // Pipeline: Percept -> Rag -> Advice -> Critic
                val result = com.aistudio.detected.stress.agents.Orchestrator.analyze(text, emptySet()) // We can pass likedTitles if we add it to ViewModel
                
                val geminiResponse = GeminiResponse(
                    has_stress = result.hasStress,
                    category_tag = result.category,
                    empathy_message = result.empathyMessage,
                    search_keywords = result.searchKeywords
                )
                
                _uiState.value = AppState.Success(geminiResponse, result.adviceList)
            } catch (e: Exception) {
                _uiState.value = AppState.Error("خطا در تحلیل اطلاعات: ${e.message}")
            }
        }
    }
}
