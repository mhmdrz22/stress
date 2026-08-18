package com.aistudio.detected.stress.ui.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aistudio.detected.stress.ui.components.VideoSuggestionsCarousel
import com.aistudio.detected.stress.ui.components.ArameshSunriseLogo
import com.aistudio.detected.stress.ui.theme.ArameshTheme
import com.aistudio.detected.stress.viewmodel.ChatIntent
import com.aistudio.detected.stress.viewmodel.ChatViewModel
import com.aistudio.detected.stress.data.local.ChatMessage
import kotlinx.coroutines.launch

val SoftSand = Color(0xFFF9F7F1)
val SageGreen = Color(0xFF8DA399)
val SunriseGold = Color(0xFFE5B96E)
val DarkEarth = Color(0xFF4A5D53)
val BubbleGray = Color(0xFFFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(onNavigateStats: () -> Unit, onNavigateAdmin: () -> Unit = {}, onNavigateAssessment: () -> Unit = {}, viewModel: ChatViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    
    var isMicLoading by remember { mutableStateOf(false) }
    
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isMicLoading = false
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val newText = (state.inputText + " " + matches[0]).trim()
                viewModel.processIntent(ChatIntent.UpdateInput(newText))
                viewModel.processIntent(ChatIntent.SubmitAnalysis)
            }
        }
    }
    
    LaunchedEffect(state.chatMessages.size, state.isLoading) {
        if (state.chatMessages.isNotEmpty() || state.isLoading) {
            coroutineScope.launch {
                val targetIndex = if (state.isLoading) state.chatMessages.size else state.chatMessages.size - 1
                if (targetIndex >= 0) {
                    listState.animateScrollToItem(targetIndex)
                }
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                Surface(
                    color = SoftSand,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = onNavigateAdmin) {
                            Icon(Icons.Default.Lock, contentDescription = "پنل مدیریت", tint = SageGreen)
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ArameshSunriseLogo(modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "آرامشیار",
                                color = DarkEarth,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Row {
                            IconButton(onClick = onNavigateAssessment) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "ارزیابی استرس", tint = SageGreen)
                            }
                            IconButton(onClick = onNavigateStats) {
                                Icon(Icons.Default.DateRange, contentDescription = "تاریخچه", tint = SageGreen)
                            }
                            IconButton(onClick = { viewModel.processIntent(ChatIntent.ClearResult) }) {
                                Icon(Icons.Default.Refresh, contentDescription = "شروع مجدد", tint = SageGreen)
                            }
                        }
                    }
                }
            },
            containerColor = SoftSand
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // بخش پیامها
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    reverseLayout = false
                ) {
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    
                    state.assessmentResult?.let { assessment ->
                        item {
                            val levelText = when (assessment.level) {
                                com.aistudio.detected.stress.data.StressLevel.LOW ->
                                    "سطح بررسیشده: پایین"

                                com.aistudio.detected.stress.data.StressLevel.MODERATE ->
                                    "سطح بررسیشده: متوسط"

                                com.aistudio.detected.stress.data.StressLevel.HIGH ->
                                    "سطح بررسیشده: بالا"

                                com.aistudio.detected.stress.data.StressLevel.URGENT ->
                                    "نیاز به کمک فوری"
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = when (assessment.level) {
                                    com.aistudio.detected.stress.data.StressLevel.LOW ->
                                        Color(0xFFE8F5E9)

                                    com.aistudio.detected.stress.data.StressLevel.MODERATE ->
                                        Color(0xFFFFF8E1)

                                    com.aistudio.detected.stress.data.StressLevel.HIGH,
                                    com.aistudio.detected.stress.data.StressLevel.URGENT ->
                                        Color(0xFFFFEBEE)
                                }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = levelText,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkEarth
                                    )

                                    Text(
                                        text = assessment.disclaimer,
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }

                    if (state.chatMessages.isEmpty() && !state.isLoading) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "سلام! من آرامش‌یار هستم.\nامروز چه احساسی داری؟",
                                    color = DarkEarth,
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    
                    items(state.chatMessages) { msg ->
                        BeautifulChatBubble(
                            message = msg, 
                            onQuickReply = { text ->
                                viewModel.processIntent(ChatIntent.UpdateInput(text))
                                viewModel.processIntent(ChatIntent.SubmitAnalysis)
                            },
                            onFeedback = { isLiked ->
                                // For simplicity, we trigger the feedback based on the last advice category
                                // The ViewModel already supports ChatIntent.SubmitFeedback or ToggleLike
                                viewModel.processIntent(ChatIntent.SubmitFeedback(isLiked))
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    if (state.isLoading) {
                        item {
                            TypingIndicatorBubble()
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                // باکس تایپ پیام
                Surface(
                    color = BubbleGray,
                    shadowElevation = 8.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = state.inputText,
                            onValueChange = { viewModel.processIntent(ChatIntent.UpdateInput(it)) },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("امروز چه احساسی داری؟...", color = Color.Gray) },
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SageGreen,
                                unfocusedBorderColor = Color.LightGray,
                                focusedContainerColor = SoftSand,
                                unfocusedContainerColor = SoftSand
                            )
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        // دکمه ارسال با رنگ سبز ملایم
                        IconButton(
                            onClick = {
                                if (state.inputText.isNotBlank()) {
                                    viewModel.processIntent(ChatIntent.SubmitAnalysis)
                                }
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .background(SageGreen, RoundedCornerShape(50))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send, 
                                contentDescription = "ارسال", 
                                tint = BubbleGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BeautifulChatBubble(
    message: ChatMessage, 
    onQuickReply: (String) -> Unit = {},
    onFeedback: (Boolean) -> Unit = {},
    onAdviceFeedback: (String, Boolean) -> Unit = { _, _ -> }
) {
    val isUser = message.sender == "user"
    val parts = message.content.split("|||")
    val text = parts[0]
    val category = if (parts.size > 1) parts[1] else "joy"
    val keywords = if (parts.size > 2 && parts[2].isNotBlank()) parts[2].split(",") else emptyList()
    val actualKeywords = if (parts.size == 2) parts[1].split(",") else keywords
    val adviceIds = if (parts.size > 3 && parts[3].isNotBlank()) parts[3].split(",") else emptyList()
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 0.dp,
                    bottomEnd = if (isUser) 0.dp else 16.dp
                ),
                color = if (isUser) SageGreen else BubbleGray,
                shadowElevation = if (isUser) 0.dp else 2.dp,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = text,
                        color = if (isUser) BubbleGray else DarkEarth,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                    
                    if (!isUser) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            var feedbackGiven by remember { mutableStateOf<Boolean?>(null) }
                            
                            if (feedbackGiven != false) {
                                Text(
                                    text = "👍", 
                                    modifier = Modifier.clickable { feedbackGiven = true; onFeedback(true) }.padding(horizontal = 4.dp),
                                    fontSize = 14.sp,
                                    color = if (feedbackGiven == true) SageGreen else Color.Gray
                                )
                            }
                            if (feedbackGiven != true) {
                                Text(
                                    text = "👎", 
                                    modifier = Modifier.clickable { feedbackGiven = false; onFeedback(false) }.padding(horizontal = 4.dp),
                                    fontSize = 14.sp,
                                    color = if (feedbackGiven == false) Color.Red else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
        
        if (!isUser) {
            Spacer(modifier = Modifier.height(8.dp))
            val actualCategory = if (category.isEmpty()) "general" else category
            QuickReplies(actualCategory, onQuickReply)
        }
        
        if (!isUser && actualKeywords.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "پیشنهاد برای شما:",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            VideoSuggestionsCarousel(queries = actualKeywords)
        }
    }
}

@Composable
fun TypingIndicatorBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 0.dp,
                bottomEnd = 16.dp
            ),
            color = BubbleGray,
            shadowElevation = 2.dp
        ) {
            val transition = rememberInfiniteTransition()
            
            val alpha1 by transition.animateFloat(
                initialValue = 0.2f, targetValue = 1f,
                animationSpec = infiniteRepeatable(animation = tween(400), repeatMode = RepeatMode.Reverse)
            )
            val alpha2 by transition.animateFloat(
                initialValue = 0.2f, targetValue = 1f,
                animationSpec = infiniteRepeatable(animation = tween(400, delayMillis = 200), repeatMode = RepeatMode.Reverse)
            )
            val alpha3 by transition.animateFloat(
                initialValue = 0.2f, targetValue = 1f,
                animationSpec = infiniteRepeatable(animation = tween(400, delayMillis = 400), repeatMode = RepeatMode.Reverse)
            )
            
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(DarkEarth.copy(alpha = alpha1)))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(DarkEarth.copy(alpha = alpha2)))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(DarkEarth.copy(alpha = alpha3)))
            }
        }
    }
}

@Composable
fun QuickReplyChip(text: String, onClick: (String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF1F5F9),
        onClick = { onClick(text) }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            fontSize = 12.sp,
            color = DarkEarth
        )
    }
}

@Composable
fun QuickReplies(category: String, onQuickReply: (String) -> Unit) {
    val replies = when (category) {
        "exam_stress" -> listOf("چطور تمرکز کنم؟", "استرس امتحان دارم")
        "anxiety" -> listOf("چیکار کنم آروم شم؟", "نفس عمیق یادم بده")
        "sleep" -> listOf("چند ساعت بخوابم؟", "بیخوابی دارم")
        "anger" -> listOf("چطور خشمم رو کنترل کنم؟", "آروم شدم")
        "burnout" -> listOf("چطور انرژی بگیرم؟", "خسته‌ام از همه چی")
        "depression" -> listOf("حالم خیلی بده", "کسی هست کمکم کنه؟")
        "joy" -> listOf("ممنون آرامشیار", "امروز حالم خوبه")
        else -> listOf("بیشتر توضیح بده", "الان حالم بهتره")
    }
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        replies.forEach { reply ->
            QuickReplyChip(reply, onQuickReply)
        }
    }
}
