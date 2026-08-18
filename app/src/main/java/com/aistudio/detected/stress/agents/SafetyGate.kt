package com.aistudio.detected.stress.agents

/**
 * Local, deterministic safety screening.
 * It is intentionally separate from sentiment classification and generative AI.
 */
enum class SafetyStatus {
    CLEAR,
    NEEDS_CHECK_IN,
    URGENT
}

data class SafetyGateResult(
    val status: SafetyStatus,
    val userFacingMessage: String? = null,
    val matchedRuleId: String? = null
)

object SafetyGate {
    private val urgentPhrases = listOf(
        "میخوام خودکشی کنم",
        "می خواهم خودکشی کنم",
        "قصد خودکشی دارم",
        "میخوام به خودم آسیب بزنم",
        "می خواهم به خودم آسیب بزنم",
        "نمی توانم امن بمانم",
        "نمیتونم امن بمونم",
        "kill myself",
        "want to kill myself",
        "end my life",
        "hurt myself",
        "cannot stay safe"
    )

    private val checkInPhrases = listOf(
        "دیگه نمی کشم",
        "دیگه نمیکشم",
        "کاش نبودم",
        "نمیخوام ادامه بدم",
        "نمی خواهم ادامه بدهم",
        "i give up",
        "no reason to live",
        "i feel hopeless"
    )

    fun evaluate(text: String): SafetyGateResult {
        val normalized = normalize(text)

        urgentPhrases.firstOrNull { normalized.contains(normalize(it)) }?.let { phrase ->
            return SafetyGateResult(
                status = SafetyStatus.URGENT,
                userFacingMessage = urgentMessage(),
                matchedRuleId = "urgent:${phrase.take(24)}"
            )
        }

        checkInPhrases.firstOrNull { normalized.contains(normalize(it)) }?.let { phrase ->
            return SafetyGateResult(
                status = SafetyStatus.NEEDS_CHECK_IN,
                userFacingMessage = checkInMessage(),
                matchedRuleId = "check_in:${phrase.take(24)}"
            )
        }

        return SafetyGateResult(SafetyStatus.CLEAR)
    }

    fun urgentMessage(): String =
        "متأسفم که در این وضعیت سخت هستی. اگر ممکن است تنها نمان و همین حالا با یک فرد مورداعتماد تماس بگیر. " +
            "اگر در خطر فوری هستی یا نمی‌توانی ایمن بمانی، با خدمات اورژانسی محلی تماس بگیر. " +
            "در ایران: ۱۲۳ اورژانس اجتماعی و ۱۴۸۰ صدای مشاور."

    fun checkInMessage(): String =
        "متأسفم که این‌قدر تحت فشار هستی. آیا الان در خطر فوری هستی یا فکر آسیب‌زدن به خودت داری؟"

    private fun normalize(text: String): String = text
        .lowercase()
        .replace('ي', 'ی')
        .replace('ك', 'ک')
        .replace(Regex("\\s+"), " ")
        .trim()
}
