package com.aistudio.detected.stress.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SoftGreen,
    onPrimary = Color.White,
    secondary = WarmWood,
    onSecondary = Color.White,
    background = CalmCream,
    onBackground = TextPrimary,
    surface = CalmCream,
    onSurface = TextPrimary
)

@Composable
fun DetectedTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}
