package com.aistudio.detected.stress.agents

object PerceptionAgent {

    private val persianChars = Regex("[\u0600-\u06FF]")
    
    private val englishNegative = listOf(
        "stress", "anxious", "worried", "tired", "depressed", "angry",
        "sad", "can't", "cant", "cannot", "unable", "focus", "exam",
        "fail", "fear", "panic", "burnout", "sleep", "insomnia",
        "hopeless", "frustrated", "overwhelmed", "lonely", "empty",
        "dont", "dont have", "nervous", "tension", "pressure"
    )

    private val persianRoots = mapOf(
        "استرس" to listOf("استرس", "استرس دارم", "استرسی", "استرسم", "اضطراب"),
        "خسته" to listOf("خسته", "خستگی", "خستم", "خسته‌ام", "فرسوده"),
        "نگران" to listOf("نگران", "نگرانی", "نگرون", "نگرانم", "دلواپس"),
        "عصبی" to listOf("عصبی", "عصبانی", "خشم", "عصبانیتم", "دلخور", "عصبانیم"),
        "افسرده" to listOf("افسرده", "افسردگی", "غمگین", "ناامید", "یأس", "دپرس"),
        "خواب" to listOf("خواب", "بی‌خوابی", "خوابم نمیبره", "بی خواب", "بیخوابی"),
        "امتحان" to listOf("امتحان", "کنکور", "آزمون", "درس", "معدل", "نمره", "دانشگاه"),
        "تمرکز" to listOf("تمرکز", "حواس", "حواسم پرت", "تمرکز ندارم", "حواسم جمع نیست"),
        "بد" to listOf("بد", "افتضاح", "خراب", "disaster", "فاجعه", "وحشتناک")
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
        val normalized = text.lowercase().trim()
        val isPersian = persianChars.containsMatchIn(text)
        val hasEnglishLetters = normalized.any { it in 'a'..'z' }
        val lang = when {
            isPersian && hasEnglishLetters -> "mixed"
            isPersian -> "fa"
            else -> "en"
        }

        var stressScore = 0
        val detectedKeywords = mutableListOf<String>()

        // === Persian Analysis ===
        if (isPersian) {
            for ((root, variants) in persianRoots) {
                if (variants.any { normalized.contains(it) }) {
                    stressScore += when (root) {
                        "استرس", "خسته", "افسرده" -> 3
                        "نگران", "عصبی" -> 2
                        else -> 1
                    }
                    detectedKeywords.add(root)
                }
            }
        }

        // === English Analysis ===
        if (hasEnglishLetters || lang == "mixed") {
            for (word in englishNegative) {
                if (normalized.contains(word)) {
                    stressScore += 2
                    detectedKeywords.add(word)
                }
            }
        }

        // === Context Amplification ===
        if (detectedKeywords.contains("امتحان") || detectedKeywords.contains("exam")) {
            if (detectedKeywords.contains("تمرکز") || detectedKeywords.contains("focus")) {
                stressScore += 3
            }
        }
        if (detectedKeywords.contains("cant") || detectedKeywords.contains("cannot") || detectedKeywords.contains("unable")) {
            stressScore += 2
        }

        val hasStress = stressScore > 0
        val severity = (stressScore * 10).coerceAtMost(100)

        val category = when {
            detectedKeywords.contains("عصبی") || detectedKeywords.contains("angry") -> "anger"
            detectedKeywords.contains("خواب") || detectedKeywords.contains("sleep") -> "sleep"
            detectedKeywords.contains("خسته") || detectedKeywords.contains("tired") -> "burnout"
            detectedKeywords.contains("افسرده") || detectedKeywords.contains("depressed") -> "depression"
            detectedKeywords.contains("امتحان") || detectedKeywords.contains("exam") -> "exam_stress"
            detectedKeywords.contains("تمرکز") || detectedKeywords.contains("focus") -> "anxiety"
            hasStress -> "anxiety"
            else -> "joy"
        }

        val confidence = when {
            stressScore >= 5 -> 0.9f
            stressScore >= 3 -> 0.75f
            stressScore >= 1 -> 0.6f
            else -> 0.2f
        }

        return AnalysisResult(
            hasStress = hasStress,
            severity = severity,
            category = category,
            confidence = confidence,
            isCrisis = false,
            detectedLanguage = lang,
            extractedKeywords = detectedKeywords.distinct()
        )
    }
}
