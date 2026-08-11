import re

with open('app/src/main/java/com/aistudio/detected/stress/ui/screens/MainScreen.kt', 'r', encoding='utf-8') as f:
    content = f.read()

# Insert enum at top
enum_str = "enum class UiMode { INPUT, LOADING, ERROR, RESULT }\n\n@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)"
content = content.replace("@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)", enum_str)

# Calculate currentUiMode
mode_calc = """
    val currentUiMode = when {
        state.isLoading -> UiMode.LOADING
        state.error != null -> UiMode.ERROR
        state.result != null -> UiMode.RESULT
        else -> UiMode.INPUT
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {"""
content = content.replace("    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {", mode_calc)

# Update AnimatedContent
old_anim = """                AnimatedContent(
                    targetState = state,"""
new_anim = """                AnimatedContent(
                    targetState = currentUiMode,"""
content = content.replace(old_anim, new_anim)

# Update the when block
old_when = """                ) { currentState ->
                    when {"""
new_when = """                ) { targetMode ->
                    when (targetMode) {"""
content = content.replace(old_when, new_when)

# Update the branches inside when
content = content.replace("currentState.isLoading -> {", "UiMode.LOADING -> {")
content = content.replace("currentState.error != null -> {", "UiMode.ERROR -> {")
content = content.replace("currentState.result != null -> {", "UiMode.RESULT -> {")
content = content.replace("else -> {", "UiMode.INPUT -> {")

# In the RESULT branch, there might be references to currentState, change them to state
content = content.replace("currentState.error", "state.error")
content = content.replace("val data = currentState.result", "val data = state.result")
content = content.replace("currentState.adviceList", "state.adviceList")
# Update the INPUT branch
content = content.replace("value = currentState.inputText", "value = state.inputText")

with open('app/src/main/java/com/aistudio/detected/stress/ui/screens/MainScreen.kt', 'w', encoding='utf-8') as f:
    f.write(content)

