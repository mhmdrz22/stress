package com.aistudio.detected.stress.agents

enum class SafetyStatus {
    CLEAR,
    NEEDS_CHECK_IN,
    URGENT
}

data class SafetyGateResult(
    val status: SafetyStatus,
    val message: String? = null
)

object SafetyGate {
    private val urgentPhrases = listOf(
        "میخوام خودکشی کنم",
        "می خواهم خودکشی کنم",
        "قصد خودکشی دارم",
        "میخوام به خودم آسیب بزنم",
        "می خواهم به خودم آسیب بزنم",
        "نمی تونم خودم را کنترل کنم",
        "نمیتونم خودم را کنترل کنم",
        "نمی توانم امن بمانم",
        "نمی تونم امن بمونم",
        "kill myself",
        "want to kill myself",
        "end my life",
        "hurt myself",
        "cannot stay safe"
    )

    private val checkInPhrases = listOf(
        "دیگه نمی کشم",
        "زندگی برام سخته",
        "هیچ امیدی ندارم",
        "کاش نبودم",
        "نمیخوام ادامه بدم",
        "i give up",
        "no reason to live",
        "i feel hopeless"
    )

    fun evaluate(text: String): SafetyGateResult {
        val normalized = text
            .lowercase()
            .replace('ي', 'ی')
            .replace('ك', 'ک')
            .trim()

        return when {
            urgentPhrases.any { normalized.contains(it) } -> SafetyGateResult(
                status = SafetyStatus.URGENT,
                message = urgentMessage()
            )

            checkInPhrases.any { normalized.contains(it) } -> SafetyGateResult(
                status = SafetyStatus.NEEDS_CHECK_IN,
                message = "متأسفم که اینقدر تحت فشار هستی. آیا الان در خطر فوری هستی یا فکر آسیب‌زدن به خودت داری؟"
            )

            else -> SafetyGateResult(SafetyStatus.CLEAR)
        }
    }

    fun urgentMessage(): String =
        "متأسفم که در این وضعیت سخت هستی. اگر ممکن است تنها نمان و همین حالا با یک فرد مورد‌اعتماد تماس بگیر. " +
            "اگر در خطر فوری هستی یا نمی‌توانی ایمن بمانی، با خدمات اورژانسی محلی تماس بگیر. " +
            "در ایران: ۱۲۳ اورژانس اجتماعی و ۱۴۸۰ صدای مشاور."
}