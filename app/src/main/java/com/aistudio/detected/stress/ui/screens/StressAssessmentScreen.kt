package com.aistudio.detected.stress.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aistudio.detected.stress.data.StressAssessmentEngine
import com.aistudio.detected.stress.data.StressLevel

private val questions = listOf(
    "در یک هفتهٔ گذشته، چقدر احساس تنش یا فشار داشته‌ای؟",
    "چقدر کنترل‌کردن نگرانی‌ها برایت دشوار بوده است؟",
    "چقدر استرس روی خواب، تمرکز یا کارهای روزانه‌ات اثر گذاشته است؟",
    "چقدر احساس خستگی یا فرسودگی داشته‌ای؟"
)

@Composable
fun StressAssessmentScreen(
    onResult: (level: StressLevel, score: Int, maxScore: Int) -> Unit
) {
    var answers by remember { mutableStateOf(List<Int?>(questions.size) { null }) }
    var immediateConcern by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "بررسی کوتاه استرس",
            style = MaterialTheme.typography.headlineSmall
        )

        Text("این ابزار تشخیص پزشکی نیست و فقط برای بررسی اولیه طراحی شده است.")

        questions.forEachIndexed { questionIndex, question ->
            Column {
                Text(question)

                (0..4).forEach { value ->
                    val label = listOf(
                        "هرگز",
                        "به‌ندرت",
                        "گاهی",
                        "اغلب",
                        "تقریباً همیشه"
                    )[value]

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = answers[questionIndex] == value,
                            onClick = {
                                answers = answers.toMutableList().also {
                                    it[questionIndex] = value
                                }
                                error = null
                            }
                        )
                        Text(label, modifier = Modifier.padding(top = 12.dp))
                    }
                }
            }
        }

        Row {
            Checkbox(
                checked = immediateConcern,
                onCheckedChange = { immediateConcern = it }
            )
            Text(
                "الان احساس می‌کنم در خطر فوری هستم یا ممکن است نتوانم ایمن بمانم.",
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (answers.any { it == null }) {
                    error = "لطفاً به همهٔ پرسش‌ها پاسخ بده."
                    return@Button
                }

                val result = StressAssessmentEngine.assess(
                    input = com.aistudio.detected.stress.data.StressAssessmentInput(
                        answers = answers.filterNotNull(),
                        hasImmediateSafetyConcern = immediateConcern
                    )
                )

                onResult(result.level, result.totalScore, result.maxScore)
            }
        ) {
            Text("نمایش نتیجه")
        }
    }
}
