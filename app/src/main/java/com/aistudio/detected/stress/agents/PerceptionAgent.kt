package com.aistudio.detected.stress.agents

object PerceptionAgent {
    /**
     * Agent 1: Perception & Intent Analyzer (تحلیلگر عمیق هیجان)
     */
    fun analyze(text: String): PerceptionResult {
        val negativeWords = listOf("نگران", "خسته", "عصب", "بد", "مشکل", "خواب", "استرس", "ناامید", "خالی", "درد", "غمگین", "افسرد")
        val urgentWords = listOf("خودکشی", "مردن", "خسته شدم", "تمومش کنم", "هیچکس", "کشتن", "ببرم")
        
        var score = 0
        var urgencyScore = 0
        
        // Simple Stemmer for Persian
        fun stem(word: String): String {
            var w = word.replace("\u200c", "") // remove ZWNJ
            val suffixes = listOf("ها", "های", "هایی", "ان", "ات", "ین", "یم", "ید", "ند", "م", "ت", "ش", "ی", "گی")
            for (suffix in suffixes) {
                if (w.endsWith(suffix) && w.length > suffix.length + 2) {
                    w = w.removeSuffix(suffix)
                    break
                }
            }
            return w
        }
        
        val words = text.split(Regex("\\s+"))
        for (w in words) {
            val stemmed = stem(w)
            if (negativeWords.any { stemmed.contains(it) }) score += 2
        }
        
        for (urgent in urgentWords) {
            if (text.contains(urgent)) urgencyScore += 5
        }
        
        val hasStress = score > 0 || urgencyScore > 0
        val severity = minOf((score * 15) + (urgencyScore * 50), 100)
        
        val isCrisis = urgencyScore > 0 || severity > 80
        
        val category = when {
            text.contains("عصب") || text.contains("خشم") -> "anger"
            text.contains("خواب") || text.contains("بیدار") -> "sleep"
            text.contains("خسته") || text.contains("فرسوده") || text.contains("برید") -> "burnout"
            text.contains("غم") || text.contains("افسرد") || text.contains("ناامید") -> "depression"
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
