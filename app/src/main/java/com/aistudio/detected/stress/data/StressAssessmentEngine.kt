package com.aistudio.detected.stress.data

enum class StressLevel {
    LOW,
    MODERATE,
    HIGH,
    URGENT
}

data class StressAssessmentInput(
    val answers: List<Int>,
    val hasImmediateSafetyConcern: Boolean = false
)

data class StressAssessmentResult(
    val totalScore: Int,
    val maxScore: Int,
    val level: StressLevel,
    val assessmentVersion: String,
    val disclaimer: String,
    val recommendedActions: List<String>,
    val shouldEscalate: Boolean
)

object StressAssessmentEngine {
    const val ASSESSMENT_VERSION = "1.0"
    const val MIN_ANSWER = 0
    const val MAX_ANSWER = 4

    fun assess(input: StressAssessmentInput): StressAssessmentResult {
        require(input.answers.isNotEmpty()) {
            "At least one answer is required."
        }
        require(input.answers.all { it in MIN_ANSWER..MAX_ANSWER }) {
            "Answers must be between $MIN_ANSWER and $MAX_ANSWER."
        }

        val total = input.answers.sum()
        val max = input.answers.size * MAX_ANSWER

        val level = when {
            input.hasImmediateSafetyConcern -> StressLevel.URGENT
            total.toDouble() / max < 0.34 -> StressLevel.LOW
            total.toDouble() / max < 0.67 -> StressLevel.MODERATE
            else -> StressLevel.HIGH
        }

        return StressAssessmentResult(
            totalScore = total,
            maxScore = max,
            level = level,
            assessmentVersion = ASSESSMENT_VERSION,
            disclaimer = "این ارزیابی تشخیص پزشکی یا جایگزین کمک تخصصی نیست.",
            recommendedActions = actionsFor(level),
            shouldEscalate = level == StressLevel.HIGH || level == StressLevel.URGENT
        )
    }

    private fun actionsFor(level: StressLevel): List<String> = when (level) {
        StressLevel.LOW -> listOf(
            "چند دقیقه مکث کن و به تنفس خود توجه کن.",
            "خواب، آب کافی و کمی حرکت ملایم را در برنامه‌ات نگه دار."
        )

        StressLevel.MODERATE -> listOf(
            "همین حالا یک تمرین تنفس آرام یا grounding کوتاه انجام بده.",
            "فقط یک کار کوچک و قابل‌انجام برای ادامهٔ امروز انتخاب کن.",
            "اگر این حالت ادامه یافت یا کارهای روزمره را مختل کرد، با متخصص صحبت کن."
        )

        StressLevel.HIGH -> listOf(
            "در یک جای امن چند دقیقه توقف کن و تمرین grounding انجام بده.",
            "امروز با یک فرد مورد‌اعتماد تماس بگیر یا حرف بزن.",
            "برای دریافت کمک حرفه‌ای از روانشناس یا پزشک اقدام کن."
        )

        StressLevel.URGENT -> listOf(
            "اگر در خطر فوری هستی یا نمی‌توانی ایمن بمانی، با خدمات اورژانسی محل زندگی‌ات تماس بگیر.",
            "اگر ممکن است تنها نمان و با یک فرد مورد‌اعتماد در نزدیکی‌ات ارتباط بگیر.",
            "در ایران می‌توانی با ۱۲۳ اورژانس اجتماعی یا ۱۴۸۰ صدای مشاور تماس بگیری."
        )
    }
}