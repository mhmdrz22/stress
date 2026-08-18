package com.aistudio.detected.stress.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.detected.stress.ui.theme.ArameshTheme

@Composable
fun VideoSuggestionsCarousel(queries: List<String>) {
    val context = LocalContext.current
    val colors = listOf(
        listOf(Color(0xFF8B6B4A), Color(0xFF6B806D)),
        listOf(Color(0xFF6B806D), Color(0xFF4A6B8B)),
        listOf(Color(0xFF4A6B8B), Color(0xFF8B4A6B)),
        listOf(Color(0xFF8B4A6B), Color(0xFF8B6B4A))
    )
    
    val durations = listOf("10 دقیقه", "5 دقیقه", "15 دقیقه", "8 دقیقه", "12 دقیقه")

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        itemsIndexed(queries) { index, queryData ->
            val parts = queryData.split("~")
            val title = parts[0]
            val actualUrl = if (parts.size > 1) parts[1] else ""
            
            VideoCard(
                query = title,
                gradientColors = colors[index % colors.size],
                duration = durations[index % durations.size],
                actualUrl = actualUrl,
                onPlatformClick = { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun VideoCard(query: String, gradientColors: List<Color>, duration: String, actualUrl: String = "", onPlatformClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(260.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Thumbnail Area (Mocked with Gradient)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Brush.linearGradient(gradientColors)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                // Duration Tag
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = duration,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Content Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = query,
                    style = ArameshTheme.typography.title.copy(fontSize = 16.sp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Platforms
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تماشا در:",
                        style = ArameshTheme.typography.label.copy(fontSize = 12.sp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (actualUrl.isNotEmpty()) {
                            val platformName = if (actualUrl.contains("youtube")) "یوتیوب" else if (actualUrl.contains("aparat")) "آپارات" else "لینک مستقیم"
                            val platformColor = if (actualUrl.contains("youtube")) Color(0xFFEF4444) else if (actualUrl.contains("aparat")) Color(0xFF9333EA) else Color(0xFF0EA5E9)
                            val platformBg = if (actualUrl.contains("youtube")) Color(0xFFFEE2E2) else if (actualUrl.contains("aparat")) Color(0xFFF3E8FF) else Color(0xFFE0F2FE)
                            
                            PlatformTag(
                                name = "باز کردن ($platformName)",
                                color = platformColor,
                                bgColor = platformBg,
                                onClick = { onPlatformClick(actualUrl) }
                            )
                        } else {
                            // Fallback to youtube search if no direct URL is provided
                            PlatformTag(
                                name = "جستجوی یوتیوب",
                                color = Color(0xFFEF4444),
                                bgColor = Color(0xFFFEE2E2),
                                onClick = { onPlatformClick("https://www.youtube.com/results?search_query=${Uri.encode(query)}") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlatformTag(name: String, color: Color, bgColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
