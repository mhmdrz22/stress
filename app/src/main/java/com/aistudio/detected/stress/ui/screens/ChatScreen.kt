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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aistudio.detected.stress.ui.components.GlassmorphismInputCard
import com.aistudio.detected.stress.ui.components.VideoSuggestionsCarousel
import com.aistudio.detected.stress.ui.theme.ArameshTheme
import com.aistudio.detected.stress.viewmodel.ChatIntent
import com.aistudio.detected.stress.viewmodel.ChatViewModel
import com.aistudio.detected.stress.data.local.ChatMessage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(onNavigateStats: () -> Unit, onNavigateAdmin: () -> Unit = {}, viewModel: ChatViewModel = viewModel()) {
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
            containerColor = ArameshTheme.colors.background,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("آرامش‌یار", style = ArameshTheme.typography.title, color = ArameshTheme.colors.accentWood)
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateAdmin) {
                            Icon(Icons.Default.Lock, contentDescription = "پنل مدیریت", tint = ArameshTheme.colors.accentWood)
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateStats) {
                            Icon(Icons.Default.DateRange, contentDescription = "تاریخچه", tint = ArameshTheme.colors.accentWood)
                        }
                        IconButton(onClick = { viewModel.processIntent(ChatIntent.ClearResult) }) {
                            Icon(Icons.Default.Refresh, contentDescription = "شروع مجدد", tint = ArameshTheme.colors.accentWood)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    if (state.chatMessages.isEmpty() && !state.isLoading) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "سلام! من آرامش‌یار هستم.\nامروز چه احساسی داری؟",
                                style = ArameshTheme.typography.title,
                                color = ArameshTheme.colors.primaryText,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.chatMessages) { message ->
                                ChatBubble(message = message, onQuickReply = { text ->
                                    viewModel.processIntent(ChatIntent.UpdateInput(text))
                                    viewModel.processIntent(ChatIntent.SubmitAnalysis)
                                })
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            if (state.isLoading) {
                                item {
                                    TypingIndicatorBubble()
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    GlassmorphismInputCard(
                        isMicLoading = isMicLoading,
                        value = state.inputText,
                        onValueChange = { viewModel.processIntent(ChatIntent.UpdateInput(it)) },
                        onMicClick = {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR")
                            }
                            try {
                                isMicLoading = true
                                speechRecognizerLauncher.launch(intent)
                            } catch (e: ActivityNotFoundException) {
                                Toast.makeText(context, "سرویس تشخیص صدا پیدا نشد.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onSubmit = { viewModel.processIntent(ChatIntent.SubmitAnalysis) }
                    )
                }
            }
        }
    }
}

@Composable
fun TypingIndicatorBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 16.dp
                ))
                .background(ArameshTheme.colors.surfaceGlass)
                .padding(horizontal = 16.dp, vertical = 16.dp)
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
            
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Gray.copy(alpha = alpha1)))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Gray.copy(alpha = alpha2)))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Gray.copy(alpha = alpha3)))
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, onQuickReply: (String) -> Unit) {
    val isUser = message.sender == "user"
    val parts = message.content.split("|||")
    val text = parts[0]
    val category = if (parts.size > 1) parts[1] else "joy"
    val keywords = if (parts.size > 2 && parts[2].isNotBlank()) parts[2].split(",") else emptyList()
    // Backward compatibility for old messages that only had text and keywords
    val actualKeywords = if (parts.size == 2) parts[1].split(",") else keywords
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ))
                .background(if (isUser) ArameshTheme.colors.accentWood else ArameshTheme.colors.surfaceGlass)
                .padding(16.dp)
        ) {
            Text(
                text = text,
                color = if (isUser) Color.White else ArameshTheme.colors.primaryText,
                style = ArameshTheme.typography.body,
                textAlign = if (isUser) TextAlign.End else TextAlign.Start
            )
        }
        
        if (!isUser) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                val (q1, q2) = when (category) {
                    "sleep" -> Pair("چند ساعت بخوابم؟", "راهکار بی‌خوابی")
                    "anger" -> Pair("چطور آروم شم؟", "نفس عمیق")
                    "anxiety" -> Pair("خیلی استرس دارم", "چیکار کنم؟")
                    "depression" -> Pair("احساس تنهایی می‌کنم", "بیشتر حرف بزنیم")
                    "burnout" -> Pair("خیلی خسته‌ام", "نیاز به استراحت دارم")
                    else -> Pair("بیشتر توضیح بده", "الان حالم بهتره")
                }
                QuickReplyChip(q1, onQuickReply)
                QuickReplyChip(q2, onQuickReply)
            }
        }
        
        if (!isUser && actualKeywords.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "پیشنهاد برای شما:",
                style = ArameshTheme.typography.label,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            VideoSuggestionsCarousel(queries = actualKeywords)
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
            style = ArameshTheme.typography.label,
            color = ArameshTheme.colors.primaryText
        )
    }
}
