package com.aistudio.detected.stress.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class ArameshColors(
    val background: Color = Color(0xFFF9F7F1), // SoftSand
    val surfaceGlass: Color = Color(0xFFFFFFFF), // BubbleGray
    val primaryText: Color = Color(0xFF4A5D53), // DarkEarth
    val secondaryText: Color = Color(0xFF8DA399), // SageGreen
    val accentWood: Color = Color(0xFF8DA399), // SageGreen (Used for User Bubbles)
    val accentGreen: Color = Color(0xFFE5B96E), // SunriseGold
    val borderLight: Color = Color(0x338DA399)
)

val PremiumFontFamily = FontFamily.Default

@Immutable
data class ArameshTypography(
    val title: TextStyle = TextStyle(
        fontFamily = PremiumFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        color = Color(0xFF2D2C2A)
    ),
    val body: TextStyle = TextStyle(
        fontFamily = PremiumFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        color = Color(0xFF8A8782),
        lineHeight = 24.sp
    ),
    val label: TextStyle = TextStyle(
        fontFamily = PremiumFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = Color(0xFF8A8782)
    )
)

val LocalArameshColors = staticCompositionLocalOf { ArameshColors() }
val LocalArameshTypography = staticCompositionLocalOf { ArameshTypography() }

private fun materialColorFallback(arameshColors: ArameshColors) = lightColorScheme(
    primary = arameshColors.accentWood,
    onPrimary = Color.White,
    secondary = arameshColors.accentGreen,
    onSecondary = Color.White,
    background = arameshColors.background,
    onBackground = arameshColors.primaryText,
    surface = arameshColors.background,
    onSurface = arameshColors.primaryText,
    error = Color(0xFFD32F2F),
    onError = Color.White
)

@Composable
fun ArameshTheme(
    colors: ArameshColors = ArameshColors(),
    typography: ArameshTypography = ArameshTypography(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalArameshColors provides colors,
        LocalArameshTypography provides typography
    ) {
        MaterialTheme(
            colorScheme = materialColorFallback(colors),
            content = content
        )
    }
}

object ArameshTheme {
    val colors: ArameshColors
        @Composable
        get() = LocalArameshColors.current
    val typography: ArameshTypography
        @Composable
        get() = LocalArameshTypography.current
}
