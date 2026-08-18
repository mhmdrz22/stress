package com.aistudio.detected.stress.agents

/**
 * Local, non-diagnostic text categorisation.
 * Safety decisions belong exclusively to SafetyGate.
 */
object PerceptionAgent {
    private val persianChars = Regex("[\\u0600-\\u06FF]")

    private val signals = linkedMapOf(
        "anxiety" to listOf(
            "استرس", "اضطراب", "نگران", "دلواپس", "پنیک",
            "stress", "anxious", "anxiety", "worried", "panic", "nervous", "pressure"
        ),
        "sleep" to listOf(
            "بیخوابی", "بی خواب", "خوابم نمیبره", "خواب",
            "insomnia", "sleep", "cannot sleep"
        ),
        "burnout" to listOf(
            "خسته", "خستگی", "فرسوده", "بی انرژی",
            "burnout", "exhausted", "tired", "overwhelmed"
        ),
        "anger" to listOf(
            "عصبی", "عصبانی", "خشم", "کلافه",
            "angry", "anger", "frustrated", "irritated"
        ),
        "exam_stress" to listOf(
            "امتحان", "کنکور", "آزمون", "درس", "نمره",
            "exam", "test", "study", "grade"
        ),
        "low_mood" to listOf(
            "غمگین", "بی حوصله", "ناامید", "تنها",
            "sad", "low mood", "lonely", "down"
        )
    )

    data class AnalysisResult(
        val hasStress: Boolean,
        val severity: Int,
        val category: String,
        val confidence: Float,
        val isCrisis: Boolean,
        val detectedLanguage: String,
        val extractedKeywords: List<String>
    )

    fun analyze(text: String): AnalysisResult {
        val normalized = normalize(text)
        val language = detectLanguage(normalized)
        val matches = signals.mapValues { (_, terms) ->
            terms.filter { term -> normalized.contains(normalize(term)) }
        }.filterValues { it.isNotEmpty() }

        val keywordCount = matches.values.sumOf { it.size }
        val category = matches.maxByOrNull { it.value.size }?.key ?: "general"
        val hasStress = keywordCount > 0
        val severity = (keywordCount * 15).coerceAtMost(100)
        val confidence = when {
            keywordCount >= 4 -> 0.85f
            keywordCount >= 2 -> 0.70f
            keywordCount == 1 -> 0.50f
            else -> 0.20f
        }

        return AnalysisResult(
            hasStress = hasStress,
            severity = severity,
            category = category,
            confidence = confidence,
            isCrisis = false,
            detectedLanguage = language,
            extractedKeywords = matches.values.flatten().distinct()
        )
    }

    private fun detectLanguage(text: String): String {
        val hasPersian = persianChars.containsMatchIn(text)
        val hasLatin = text.any { it in 'a'..'z' }
        return when {
            hasPersian && hasLatin -> "mixed"
            hasPersian -> "fa"
            hasLatin -> "en"
            else -> "unknown"
        }
    }

    private fun normalize(text: String): String = text
        .lowercase()
        .replace('ي', 'ی')
        .replace('ك', 'ک')
        .replace(Regex("\\s+"), " ")
        .trim()
}
