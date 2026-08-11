# Add imports
sed -i 's/import com.aistudio.detected.stress.data.local.AdviceFeedback/import com.aistudio.detected.stress.data.local.AdviceFeedback\nimport com.aistudio.detected.stress.data.LocalJsonRagEngine/' app/src/main/java/com/aistudio/detected/stress/viewmodel/StressViewModel.kt

# Add localJsonRagEngine instantiation
sed -i 's/    val state: StateFlow<StressState> = _state.asStateFlow()/    val state: StateFlow<StressState> = _state.asStateFlow()\n    private val localJsonRagEngine = LocalJsonRagEngine(application)/' app/src/main/java/com/aistudio/detected/stress/viewmodel/StressViewModel.kt

# Update ClearResult to clear isOffline
sed -i 's/it.copy(result = null, adviceList = emptyList(), inputText = "")/it.copy(result = null, adviceList = emptyList(), inputText = "", isOffline = false)/' app/src/main/java/com/aistudio/detected/stress/viewmodel/StressViewModel.kt
