package com.aistudio.detected.stress.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aistudio.detected.stress.ui.theme.CalmCream
import com.aistudio.detected.stress.ui.theme.JoyOrange
import com.aistudio.detected.stress.ui.theme.JoyYellow
import com.aistudio.detected.stress.ui.theme.TextSecondary
import com.aistudio.detected.stress.viewmodel.AppState
import com.aistudio.detected.stress.viewmodel.StressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: StressViewModel = viewModel()) {
    var inputText by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Text("آرامش‌یار", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary) 
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Input Card
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(24.dp), ambientColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "امروز حالت چطوره؟",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("احساساتت رو اینجا بنویس...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                focusedContainerColor = CalmCream.copy(alpha = 0.5f),
                                unfocusedContainerColor = CalmCream.copy(alpha = 0.2f)
                            ),
                            maxLines = 8,
                            textStyle = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.analyze(inputText) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            enabled = uiState !is AppState.Loading && inputText.isNotBlank()
                        ) {
                            if (uiState is AppState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(28.dp))
                            } else {
                                Text("تحلیل و راهکار", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Result Area
                AnimatedVisibility(
                    visible = uiState is AppState.Success || uiState is AppState.Error,
                    enter = fadeIn(tween(500)) + expandVertically(tween(500)),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    when (val state = uiState) {
                        is AppState.Error -> {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDECEA)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = state.message,
                                    color = Color(0xFFD32F2F),
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        is AppState.Success -> {
                            val data = state.result
                            val isJoy = !data.has_stress
                            
                            val statusBgColor = if (isJoy) JoyYellow.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            val statusIconColor = if (isJoy) JoyOrange else MaterialTheme.colorScheme.primary
                            val statusIcon = if (isJoy) Icons.Rounded.Favorite else Icons.Rounded.SelfImprovement

                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Empathy Card
                                ElevatedCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.elevatedCardColors(containerColor = statusBgColor)
                                ) {
                                    Column(modifier = Modifier.padding(24.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = statusIcon,
                                                contentDescription = null,
                                                tint = statusIconColor,
                                                modifier = Modifier.size(32.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = if (isJoy) "حس خوب شما" else "درک احساس شما",
                                                style = MaterialTheme.typography.titleLarge,
                                                color = statusIconColor
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = data.empathy_message,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            lineHeight = 26.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Video Search Suggestions
                                if (data.search_keywords.isNotEmpty()) {
                                    Text(
                                        text = "محتوای ویدیویی پیشنهادی",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    data.search_keywords.forEach { query ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            // YouTube Button
                                            Button(
                                                onClick = {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(query)}"))
                                                    context.startActivity(intent)
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(50.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                                                shape = RoundedCornerShape(12.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFFEF4444))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("یوتیوب", color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            }

                                            // Aparat Button
                                            Button(
                                                onClick = {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.aparat.com/search/${Uri.encode(query)}"))
                                                    context.startActivity(intent)
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(50.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3E8FF)),
                                                shape = RoundedCornerShape(12.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF9333EA))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("آپارات", color = Color(0xFF7E22CE), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                }

                                // Local Advice Graph
                                if (state.adviceList.isNotEmpty()) {
                                    Text(
                                        text = "راهکارهای عملی برای شما",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    
                                    state.adviceList.forEach { item ->
                                        ElevatedCard(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 16.dp),
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                                        ) {
                                            Column(modifier = Modifier.padding(20.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = item.title,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 18.sp,
                                                        color = MaterialTheme.colorScheme.onBackground,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = CalmCream
                                                    ) {
                                                        Text(
                                                            text = item.type,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.secondary
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Text(
                                                    text = item.description,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = TextSecondary,
                                                    lineHeight = 24.sp
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}
