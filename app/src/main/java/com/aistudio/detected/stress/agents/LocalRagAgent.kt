package com.aistudio.detected.stress.agents

object LocalRagAgent {
    /**
     * Agent 3: Empathetic Communicator (عامل تولید لحن و همدلی)
     */
    fun retrieveEmpathy(perception: PerceptionResult): RagResult {
        if (perception.isCrisis) {
            return RagResult("احساس می‌کنم در شرایط بسیار سختی هستی. لطفاً یادت باشه تو تنها نیستی و آدم‌هایی هستند که می‌تونن کمک کنن.")
        }
        
        val message = when (perception.category) {
            "anger" -> "کاملاً درک می‌کنم که چقدر این موضوع عصبانیت کرده. خشم یه حس طبیعیه. دوست داری بیشتر درباره چیزی که باعث این خشم شده صحبت کنی؟"
            "sleep" -> "بی‌خوابی و خستگی می‌تونه تمام انرژی آدم رو بگیره. این مشکل از کی شروع شده؟"
            "burnout" -> "احساس فرسودگی نشون می‌ده که خیلی تلاش کردی و الان بدنت نیاز به استراحت داره. الان تو بدنت چه حسی داری؟"
            "depression" -> "می‌شنوم که روزهای سنگینی رو می‌گذرونی. من اینجام تا به حرف‌هات گوش بدم. دوست داری کمی بیشتر از این حس برام بگی؟"
            "anxiety" -> "استرس و اضطراب می‌تونه خیلی طاقت‌فرسا باشه. بیا با هم یه نفس عمیق بکشیم. چه چیزی بیشتر از همه الان نگرانت کرده؟"
            "joy" -> "چقدر عالی! خوشحالم که حس خوبی داری. امیدوارم این حال خوبت ادامه‌دار باشه."
            else -> "من اینجام و می‌شنومت. دوست داری بیشتر صحبت کنیم؟"
        }
        
        return RagResult(message)
    }
}

data class RagResult(
    val empathyMessage: String
)
