package com.aistudio.detected.stress.agents

object PerceptionAgent {
    /**
     * Agent 1: Perception & Intent Analyzer (تحلیلگر عمیق هیجان)
     */
    fun analyze(text: String): PerceptionResult {
        val negativeWords = listOf("نگران", "خسته", "عصبی", "بد", "مشکل", "خواب", "استرس", "ناامید", "خالی", "درد")
        val urgentWords = listOf("خودکشی", "مردن", "خسته شدم از زندگی", "تمومش کنم", "هیچکس", "کشتن", "ببرم")
        
        var score = 0
        var urgencyScore = 0
        
        val words = text.split(" ")
        for (word in words) {
            if (negativeWords.any { word.contains(it) }) score++
            if (urgentWords.any { word.contains(it) }) urgencyScore++
        }
        
        val hasStress = score > 0 || urgencyScore > 0
        val severity = minOf((score * 20) + (urgencyScore * 50), 100)
        
        val isCrisis = urgencyScore > 0 || severity > 80
        
        val category = when {
            text.contains("عصب") || text.contains("خشم") -> "anger"
            text.contains("خواب") || text.contains("بیدار") -> "sleep"
            text.contains("خسته") || text.contains("فرسوده") || text.contains("بریدم") -> "burnout"
            text.contains("غمگین") || text.contains("افسرده") || text.contains("ناامید") -> "depression"
            hasStress -> "anxiety"
            else -> "joy"
        }
        
        return PerceptionResult(
            hasStress = hasStress,
            severity = severity,
            category = category,
            confidence = if (text.length > 10) 0.85f else 0.4f,
            isCrisis = isCrisis
        )
    }
}

data class PerceptionResult(
    val hasStress: Boolean,
    val severity: Int,
    val category: String,
    val confidence: Float,
    val isCrisis: Boolean = false
)
