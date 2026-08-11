package com.aistudio.detected.stress.agents

object PerceptionAgent {
    
    // === Language Detection ===
    private val persianChars = Regex("[\\u0600-\\u06FF]")
    private val englishNegative = listOf(
        "stress", "anxious", "worried", "tired", "depressed", "angry", 
        "sad", "can't", "cant", "cannot", "unable", "focus", "exam", 
        "fail", "fear", "panic", "burnout", "sleep", "insomnia",
        "hopeless", "frustrated", "overwhelmed", "lonely", "empty"
    )
    private val englishUrgent = listOf(
        "suicide", "kill myself", "end my life", "die", "death", 
        "no point", "give up", "hopeless", "worthless"
    )
    
    // === Persian Stemming (Root-based matching) ===
    private val persianRoots = mapOf(
        "استرس" to listOf("استرس", "استرس دارم", "استرسی", "استرسم"),
        "خسته" to listOf("خسته", "خستگی", "خستم", "خستهام", "فرسوده"),
        "نگران" to listOf("نگران", "نگرانی", "نگرون", "نگرانم", "دلواپس"),
        "عصبی" to listOf("عصبی", "عصبانی", "خشم", "عصبانیتم", "دلخور"),
        "افسرده" to listOf("افسرده", "افسردگی", "غمگین", "ناامید", "یأس"),
        "خواب" to listOf("خواب", "بیخوابی", "خوابم نمیبره", "بی خواب"),
        "امتحان" to listOf("امتحان", "کنکور", "آزمون", "درس", "معدل", "نمره"),
        "تمرکز" to listOf("تمرکز", "حواس", "حواسم پرت", "تمرکز ندارم"),
        "بد" to listOf("بد", "افتضاح", "خراب", " disaster", "فاجعه")
    )
    
    private val persianUrgent = listOf(
        "خودکشی", "مرگ", "خودزنی", "پایان", "خسته شدم از زندگی", 
        "کشتن", "suicide", "kill", "end my life"
    )
    
    data class AnalysisResult(
        val hasStress: Boolean,
        val severity: Int, // 0-100
        val category: String,
        val confidence: Float,
        val isCrisis: Boolean,
        val detectedLanguage: String, // "fa", "en", "mixed"
        val extractedKeywords: List<String>
    )
    
    fun analyze(text: String): AnalysisResult {
        val normalized = text.lowercase().trim()
        val isPersian = persianChars.containsMatchIn(text)
        val isEnglish = normalized.any { it in 'a'..'z' }
        val lang = when {
            isPersian && isEnglish -> "mixed"
            isPersian -> "fa"
            else -> "en"
        }
        
        var stressScore = 0
        var urgencyScore = 0
        val detectedKeywords = mutableListOf<String>()
        
        // === Persian Analysis (Root Matching) ===
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
            if (persianUrgent.any { normalized.contains(it) }) {
                urgencyScore += 5
                detectedKeywords.add("CRISIS")
            }
        }
        
        // === English Analysis ===
        if (isEnglish || lang == "mixed") {
            for (word in englishNegative) {
                if (normalized.contains(word)) {
                    stressScore += 2
                    detectedKeywords.add(word)
                }
            }
            for (word in englishUrgent) {
                if (normalized.contains(word)) {
                    urgencyScore += 5
                    detectedKeywords.add("CRISIS_$word")
                }
            }
        }
        
        // === Context-based Scoring ===
        // Exam context amplifies stress
        if (detectedKeywords.contains("امتحان") || detectedKeywords.contains("exam")) {
            if (detectedKeywords.contains("تمرکز") || detectedKeywords.contains("focus")) {
                stressScore += 2
            }
        }
        
        val hasStress = stressScore > 0 || urgencyScore > 0
        val severity = minOf((stressScore * 10) + (urgencyScore * 20), 100)
        val isCrisis = urgencyScore > 0 || severity > 85
        
        val category = when {
            isCrisis -> "crisis"
            detectedKeywords.contains("عصبی") || detectedKeywords.contains("angry") -> "anger"
            detectedKeywords.contains("خواب") || detectedKeywords.contains("sleep") -> "sleep"
            detectedKeywords.contains("خسته") || detectedKeywords.contains("tired") -> "burnout"
            detectedKeywords.contains("افسرده") || detectedKeywords.contains("depressed") -> "depression"
            detectedKeywords.contains("امتحان") || detectedKeywords.contains("exam") -> "anxiety"
            hasStress -> "anxiety"
            else -> "joy"
        }
        
        // Real confidence calculation
        val confidence = when {
            isCrisis -> 0.95f
            stressScore >= 4 -> 0.85f
            stressScore >= 2 -> 0.7f
            stressScore >= 1 -> 0.55f
            else -> 0.3f
        }
        
        return AnalysisResult(
            hasStress = hasStress,
            severity = severity,
            category = category,
            confidence = confidence,
            isCrisis = isCrisis,
            detectedLanguage = lang,
            extractedKeywords = detectedKeywords.distinct()
        )
    }
}
