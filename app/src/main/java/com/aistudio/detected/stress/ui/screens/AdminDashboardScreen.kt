package com.aistudio.detected.stress.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aistudio.detected.stress.data.local.MoodEntry
import com.aistudio.detected.stress.ui.theme.ArameshTheme
import com.aistudio.detected.stress.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdminDashboardScreen(onBack: () -> Unit, viewModel: ChatViewModel = viewModel()) {
    val moods by viewModel.moodHistory.collectAsState()
    
    val totalConversations = moods.size
    val totalWithFeedback = moods.count { it.isPredictionCorrect != null }
    val correctPredictions = moods.count { it.isPredictionCorrect == true }
    
    val accuracy = if (totalWithFeedback > 0) (correctPredictions * 100 / totalWithFeedback) else 100
    // AI Usage could just be 100% since everything goes through TFLite
    val aiUsage = 100
    // Mean stress (assuming anxiety = 100, joy = 0)
    val anxietyCount = moods.count { it.hasStress }
    val meanStress = if (totalConversations > 0) (anxietyCount * 100 / totalConversations) else 0

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = { Text("پنل مدیریت", style = ArameshTheme.typography.title, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                modifier = Modifier.background(Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AdminCard(modifier = Modifier.weight(1f), title = "میانگین استرس", value = "$meanStress", icon = Icons.Default.DateRange, iconTint = Color(0xFF3B82F6))
                    AdminCard(modifier = Modifier.weight(1f), title = "کل مکالمات", value = "$totalConversations", icon = Icons.Default.Person, iconTint = Color(0xFF10B981))
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AdminCard(modifier = Modifier.weight(1f), title = "دقت مدل", value = "$accuracy%", icon = Icons.Default.CheckCircle, iconTint = Color(0xFF10B981))
                    AdminCard(modifier = Modifier.weight(1f), title = "استفاده از AI", value = "$aiUsage%", icon = Icons.Default.Face, iconTint = Color(0xFFA855F7))
                }
            }
            
            item {
                SectionTitle("توزیع سطح استرس")
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    elevation = CardDefaults.elevatedCardElevation(2.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.background(Color(0xFFFEF08A), RoundedCornerShape(16.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) { Text("کم", color = Color.Black, style = ArameshTheme.typography.label) }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("${moods.count { !it.hasStress }}", style = ArameshTheme.typography.title)
                            Text("مورد", style = ArameshTheme.typography.label, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.background(Color(0xFFFECACA), RoundedCornerShape(16.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) { Text("زیاد", color = Color.Black, style = ArameshTheme.typography.label) }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("$anxietyCount", style = ArameshTheme.typography.title)
                            Text("مورد", style = ArameshTheme.typography.label, color = Color.Gray)
                        }
                    }
                }
            }

            item {
                SectionTitle("سلامت مدل")
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    elevation = CardDefaults.elevatedCardElevation(2.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f).background(Color(0xFFFEF9C3), RoundedCornerShape(8.dp)).padding(12.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("هشدار", style = ArameshTheme.typography.label, color = Color(0xFF854D0E))
                                Text("✓ وضعیت مطلوب", style = ArameshTheme.typography.label, color = Color(0xFF854D0E))
                            }
                        }
                        Box(modifier = Modifier.weight(1f).background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp)).padding(12.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("کل بازخوردها", style = ArameshTheme.typography.label, color = Color(0xFF1D4ED8))
                                Text("$totalWithFeedback", style = ArameshTheme.typography.title, color = Color(0xFF1E3A8A))
                            }
                        }
                        Box(modifier = Modifier.weight(1f).background(Color(0xFFF0FDF4), RoundedCornerShape(8.dp)).padding(12.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("بازخوردهای مثبت", style = ArameshTheme.typography.label, color = Color(0xFF15803D))
                                Text("$accuracy%", style = ArameshTheme.typography.title, color = Color(0xFF14532D))
                            }
                        }
                    }
                }
            }

            item {
                SectionTitle("مکالمات اخیر")
            }
            items(moods.take(5)) { mood ->
                RecentMoodCard(mood)
            }

            item {
                SectionTitle("دیتاست کلمات کلیدی")
                KeywordsDatasetView()
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = ArameshTheme.typography.title,
        color = Color(0xFF1E293B),
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun AdminCard(modifier: Modifier = Modifier, title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = iconTint)
                Text(title, style = ArameshTheme.typography.label, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, style = ArameshTheme.typography.title.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold), color = iconTint, modifier = Modifier.align(Alignment.End))
        }
    }
}

@Composable
fun RecentMoodCard(mood: MoodEntry) {
    val sdf = remember { SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()) }
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (mood.isPredictionCorrect == true) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(sdf.format(Date(mood.dateMillis)), style = ArameshTheme.typography.label, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(mood.userInput, style = ArameshTheme.typography.body)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                val badgeColor = if (mood.hasStress) Color(0xFFFECACA) else Color(0xFFFEF08A)
                Box(modifier = Modifier.background(badgeColor, RoundedCornerShape(16.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) { Text(if (mood.hasStress) "زیاد" else "کم", color = Color.Black, style = ArameshTheme.typography.label) }
                Spacer(modifier = Modifier.width(8.dp))
                Text("روش: AI", style = ArameshTheme.typography.label, color = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KeywordsDatasetView() {
    val dataset = mapOf(
        "جسمی" to listOf("تپش قلب (0.85)", "لرزش (0.75)", "تنگی نفس (0.8)", "بی‌خوابی (0.8)"),
        "روانی" to listOf("اضطراب (1)", "استرس (1)", "فشار روانی (0.95)", "افسردگی (0.95)"),
        "شناختی" to listOf("نشخوار ذهنی (0.85)", "افکار منفی (0.85)", "وسواس (0.8)", "عدم تمرکز (0.75)"),
        "رفتاری" to listOf("گوشه‌گیری (0.75)", "انزوا (0.75)", "ناخن جویدن (0.65)", "کار زیاد (0.6)")
    )
    
    val colors = listOf(Color(0xFF3B82F6), Color(0xFFA855F7), Color(0xFF10B981), Color(0xFFF59E0B))

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        dataset.entries.forEachIndexed { index, entry ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(colors[index], RoundedCornerShape(4.dp)))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(entry.key, style = ArameshTheme.typography.title)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        entry.value.forEach { word ->
                            Box(modifier = Modifier.background(Color(0xFFF1F5F9), RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                                Text(word, style = ArameshTheme.typography.label, color = Color.DarkGray)
                            }
                        }
                    }
                }
            }
        }
    }
}
