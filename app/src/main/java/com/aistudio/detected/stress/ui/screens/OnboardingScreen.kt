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
import androidx.compose.ui.draw.clip

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
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    val breatheAnim by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    com.aistudio.detected.stress.ui.components.ArameshSunriseLogo(
        modifier = Modifier
            .size(150.dp)
            .graphicsLayer {
                scaleX = breatheAnim
                scaleY = breatheAnim
            }
    )
}
