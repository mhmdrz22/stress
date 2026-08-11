package com.aistudio.detected.stress.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aistudio.detected.stress.ui.theme.ArameshTheme
import kotlinx.coroutines.delay

@Composable
fun BreathingExercise() {
    var phase by remember { mutableStateOf("آماده‌ای؟") }
    
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        while (true) {
            phase = "نفس عمیق بکش..."
            delay(4000)
            phase = "آرام رها کن..."
            delay(4000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(ArameshTheme.colors.surfaceGlass),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(ArameshTheme.colors.accentGreen.copy(alpha = 0.2f))
        )
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(ArameshTheme.colors.accentGreen),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = phase,
                color = Color.White,
                style = ArameshTheme.typography.label,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
