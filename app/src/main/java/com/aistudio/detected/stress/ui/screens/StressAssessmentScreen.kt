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
import com.aistudio.detected.stress.data.StressAssessmentInput
import com.aistudio.detected.stress.data.StressAssessmentResult

private val stressQuestions = listOf(
    "در یک هفتهٔ گذشته، چقدر احساس تنش یا فشار داشتهای؟",
    "چقدر کنترلکردن نگرانیها برایت دشوار بوده است؟",
    "چقدر استرس روی خواب، تمرکز یا کارهای روزانهات اثر گذاشته است؟",
    "چقدر احساس خستگی یا فرسودگی داشتهای؟"
)

private val answerLabels = listOf(
    "هرگز",
    "بهندرت",
    "گاهی",
    "اغلب",
    "تقریباً همیشه"
)

@Composable
fun StressAssessmentScreen(
    onCompleted: (StressAssessmentResult) -> Unit,
    onBack: () -> Unit
) {
    var answers by remember {
        mutableStateOf(List<Int?>(stressQuestions.size) { null })
    }

    var immediateSafetyConcern by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

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

        Text(
            text = "این یک بررسی خوداظهاری است و تشخیص پزشکی یا جایگزین کمک حرفهای نیست.",
            style = MaterialTheme.typography.bodyMedium
        )

        stressQuestions.forEachIndexed { questionIndex, question ->
            QuestionCard(
                question = question,
                selectedAnswer = answers[questionIndex],
                onAnswerSelected = { answer ->
                    answers = answers.toMutableList().also {
                        it[questionIndex] = answer
                    }
                    errorMessage = null
                }
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Checkbox(
                checked = immediateSafetyConcern,
                onCheckedChange = {
                    immediateSafetyConcern = it
                    errorMessage = null
                }
            )

            Text(
                text = "الان احساس میکنم در خطر فوری هستم یا ممکن است نتوانم ایمن بمانم.",
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (answers.any { it == null }) {
                    errorMessage = "لطفاً به همهٔ پرسشها پاسخ بده."
                    return@Button
                }

                val result = StressAssessmentEngine.assess(
                    StressAssessmentInput(
                        answers = answers.filterNotNull(),
                        hasImmediateSafetyConcern = immediateSafetyConcern
                    )
                )

                onCompleted(result)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("نمایش نتیجه")
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("بازگشت")
        }
    }
}

@Composable
private fun QuestionCard(
    question: String,
    selectedAnswer: Int?,
    onAnswerSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.titleMedium
        )

        answerLabels.forEachIndexed { index, label ->
            Row(modifier = Modifier.fillMaxWidth()) {
                RadioButton(
                    selected = selectedAnswer == index,
                    onClick = { onAnswerSelected(index) }
                )

                Text(
                    text = label,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}
