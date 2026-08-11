package com.aistudio.detected.stress.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.aistudio.detected.stress.ui.theme.ArameshTheme
import kotlinx.coroutines.delay

@Composable
fun OnboardingScreen(onNavigateNext: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "alphaAnim"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3000)
        onNavigateNext()
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ArameshTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(32.dp)
                    .graphicsLayer { alpha = alphaAnim.value }
            ) {
                ZenLogo()
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = "آرامش‌یار",
                    style = ArameshTheme.typography.title,
                    color = ArameshTheme.colors.accentWood
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "یک نفس عمیق بکش...\nما اینجا هستیم تا بشنویم.",
                    style = ArameshTheme.typography.body,
                    color = ArameshTheme.colors.primaryText,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ZenLogo() {
    val leafColor = ArameshTheme.colors.accentGreen
    val sunColor = ArameshTheme.colors.accentWood
    
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    val breatheAnim by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    Canvas(modifier = Modifier.size(120.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        
        // Sun
        drawCircle(
            color = sunColor.copy(alpha = 0.6f),
            radius = 35.dp.toPx() * breatheAnim,
            center = center.copy(y = center.y - 10.dp.toPx())
        )
        
        // Lotus / Leaves abstract
        val stroke = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
        
        // Center petal
        drawArc(
            color = leafColor,
            startAngle = 225f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(center.x - 20.dp.toPx(), center.y - 20.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(40.dp.toPx(), 40.dp.toPx()),
            style = stroke
        )
        
        // Left petal
        drawArc(
            color = leafColor,
            startAngle = 180f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(center.x - 45.dp.toPx(), center.y),
            size = androidx.compose.ui.geometry.Size(45.dp.toPx(), 45.dp.toPx()),
            style = stroke
        )
        
        // Right petal
        drawArc(
            color = leafColor,
            startAngle = 270f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(center.x, center.y),
            size = androidx.compose.ui.geometry.Size(45.dp.toPx(), 45.dp.toPx()),
            style = stroke
        )
    }
}
