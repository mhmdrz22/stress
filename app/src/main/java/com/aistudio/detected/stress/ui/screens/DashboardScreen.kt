package com.aistudio.detected.stress.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import com.aistudio.detected.stress.data.local.MoodEntry
import com.aistudio.detected.stress.data.local.ChatMessage
import com.aistudio.detected.stress.ui.theme.ArameshTheme
import com.aistudio.detected.stress.viewmodel.ChatViewModel
import com.aistudio.detected.stress.viewmodel.ChatIntent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onBack: () -> Unit,
    viewModel: ChatViewModel
) {
    val history by viewModel.moodHistory.collectAsState()
    val allMessages by viewModel.allChatMessages.collectAsState()
    val chatState by viewModel.state.collectAsState()
    val assessmentHistory by viewModel.assessmentHistory.collectAsState()
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = ArameshTheme.colors.background,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("نمودار آرامش و تاریخچه", style = ArameshTheme.typography.title, color = ArameshTheme.colors.accentWood) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = ArameshTheme.colors.accentWood)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "وضعیت شما در روزهای اخیر",
                    style = ArameshTheme.typography.title.copy(fontSize = 18.sp),
                    color = ArameshTheme.colors.primaryText,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                if (history.isEmpty()) {
                    Text(
                        text = "هنوز اطلاعات کافی برای رسم نمودار وجود ندارد.",
                        color = ArameshTheme.colors.secondaryText,
                        style = ArameshTheme.typography.body,
                        modifier = Modifier.padding(32.dp)
                    )
                } else {
                    MoodChartCard(history.reversed())
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "تاریخچه بررسی‌ها (احساسات)",
                    style = ArameshTheme.typography.title.copy(fontSize = 18.sp),
                    color = ArameshTheme.colors.primaryText,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                history.forEach { entry ->
                    HistoryItem(entry)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "حریم خصوصی گفتگو",
                            style = ArameshTheme.typography.title.copy(fontSize = 18.sp),
                            color = ArameshTheme.colors.primaryText
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ذخیرهٔ تاریخچهٔ گفتگو",
                                    fontWeight = FontWeight.Bold,
                                    color = ArameshTheme.colors.primaryText
                                )

                                Text(
                                    text = "در حالت فعال، فقط گفتگوهای عادی روی دستگاه ذخیره میشوند. پیامهای بحران و بررسی ایمنی ذخیره نمیشوند.",
                                    style = ArameshTheme.typography.body,
                                    color = ArameshTheme.colors.secondaryText
                                )
                            }

                            Switch(
                                checked = chatState.isChatHistoryEnabled,
                                onCheckedChange = { enabled ->
                                    viewModel.processIntent(
                                        ChatIntent.SetChatHistoryEnabled(enabled)
                                    )
                                },
                                colors = SwitchDefaults.colors()
                            )
                        }

                        if (chatState.isChatHistoryEnabled) {
                            Button(
                                onClick = {
                                    viewModel.processIntent(ChatIntent.ClearChatHistory)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                Text("پاک کردن تاریخچهٔ گفتگو")
                            }
                        }
                    }
                }

                if (assessmentHistory.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "تاریخچهٔ بررسی استرس",
                        style = ArameshTheme.typography.title.copy(fontSize = 18.sp),
                        color = ArameshTheme.colors.primaryText
                    )

                    assessmentHistory.forEach { assessment ->
                        val date = SimpleDateFormat(
                            "dd MMM، HH:mm",
                            Locale("fa", "IR")
                        ).format(Date(assessment.completedAtEpochMillis))

                        val levelLabel = when (assessment.level) {
                            "LOW" -> "پایین"
                            "MODERATE" -> "متوسط"
                            "HIGH" -> "بالا"
                            else -> "ثبتشده"
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "سطح استرس: $levelLabel",
                                    fontWeight = FontWeight.Bold,
                                    color = ArameshTheme.colors.primaryText
                                )

                                Text(
                                    text = "امتیاز: ${assessment.totalScore} از ${assessment.maxScore}",
                                    color = ArameshTheme.colors.secondaryText
                                )

                                Text(
                                    text = date,
                                    style = ArameshTheme.typography.label,
                                    color = ArameshTheme.colors.secondaryText
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.processIntent(ChatIntent.ClearAssessmentHistory)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Text("پاک کردن تاریخچهٔ ارزیابی")
                    }
                }
                
                if (allMessages.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "تاریخچه گفتگوها",
                        style = ArameshTheme.typography.title.copy(fontSize = 18.sp),
                        color = ArameshTheme.colors.primaryText,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    allMessages.forEach { msg ->
                        ChatMessageItem(msg)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ChatMessageItem(msg: ChatMessage) {
    val isUser = msg.sender == "user"
    val text = msg.content.split("|||")[0]
    val formatter = SimpleDateFormat("dd MMM, HH:mm", Locale("fa", "IR"))
    val dateStr = formatter.format(Date(msg.timestamp))
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isUser) ArameshTheme.colors.surfaceGlass else Color.White)
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isUser) "شما" else "آرامش‌یار",
                    style = ArameshTheme.typography.label,
                    fontWeight = FontWeight.Bold,
                    color = if (isUser) ArameshTheme.colors.accentWood else ArameshTheme.colors.primaryText
                )
                Text(
                    text = dateStr,
                    style = ArameshTheme.typography.label.copy(fontSize = 10.sp),
                    color = ArameshTheme.colors.secondaryText
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                style = ArameshTheme.typography.body,
                color = ArameshTheme.colors.primaryText,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun MoodChartCard(data: List<MoodEntry>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(24.dp)
    ) {
        val primaryColor = ArameshTheme.colors.accentWood
        val joyColor = ArameshTheme.colors.accentGreen
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val pointWidth = if (data.size > 1) width / (data.size - 1) else width
            
            val path = Path()
            
            data.forEachIndexed { index, entry ->
                // 0 = no stress (joy), 1 = stress
                val yRatio = if (entry.hasStress) 0.8f else 0.2f
                val x = index * pointWidth
                val y = height * yRatio
                
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    val prevX = (index - 1) * pointWidth
                    val prevY = height * (if (data[index - 1].hasStress) 0.8f else 0.2f)
                    // Bezier curve
                    path.cubicTo(
                        prevX + pointWidth / 2, prevY,
                        x - pointWidth / 2, y,
                        x, y
                    )
                }
                
                // Draw dot
                drawCircle(
                    color = if (entry.hasStress) primaryColor else joyColor,
                    radius = 6.dp.toPx(),
                    center = Offset(x, y)
                )
            }
            
            drawPath(
                path = path,
                color = primaryColor.copy(alpha = 0.5f),
                style = Stroke(
                    width = 4.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
        
        // Labels
        Text("استرس/خستگی", modifier = Modifier.align(Alignment.BottomStart), style = ArameshTheme.typography.label, color = primaryColor)
        Text("آرامش/شادی", modifier = Modifier.align(Alignment.TopStart), style = ArameshTheme.typography.label, color = joyColor)
    }
}

@Composable
fun HistoryItem(entry: MoodEntry) {
    val formatter = SimpleDateFormat("dd MMM, HH:mm", Locale("fa", "IR"))
    val dateStr = formatter.format(Date(entry.dateMillis))
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateStr,
                    style = ArameshTheme.typography.label,
                    color = ArameshTheme.colors.secondaryText
                )
                Text(
                    text = entry.categoryTag.uppercase(),
                    style = ArameshTheme.typography.label,
                    color = if (entry.hasStress) ArameshTheme.colors.accentWood else ArameshTheme.colors.accentGreen
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            val details = buildString {
                if (entry.stressLevel != null) append("سطح استرس: ${entry.stressLevel}")
                if (entry.stressScore != null) append(" (نمره ${entry.stressScore})")
            }
            if (details.isNotEmpty()) {
                Text(
                    text = details,
                    style = ArameshTheme.typography.body,
                    color = ArameshTheme.colors.primaryText
                )
            }
        }
    }
}
