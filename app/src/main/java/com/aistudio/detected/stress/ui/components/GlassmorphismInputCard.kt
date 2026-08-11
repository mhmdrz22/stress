package com.aistudio.detected.stress.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aistudio.detected.stress.ui.theme.ArameshTheme

@Composable
fun CustomMicIcon(modifier: Modifier = Modifier,
    isMicLoading: Boolean = false, tint: Color) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(width = w * 0.12f, cap = StrokeCap.Round)
        
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.35f, h * 0.15f),
            size = androidx.compose.ui.geometry.Size(w * 0.3f, h * 0.45f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.15f),
            style = androidx.compose.ui.graphics.drawscope.Fill
        )
        drawArc(
            color = tint,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.2f, h * 0.3f),
            size = androidx.compose.ui.geometry.Size(w * 0.6f, h * 0.5f),
            style = stroke
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.5f, h * 0.8f),
            end = Offset(w * 0.5f, h * 0.95f),
            strokeWidth = w * 0.12f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.35f, h * 0.95f),
            end = Offset(w * 0.65f, h * 0.95f),
            strokeWidth = w * 0.12f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun GlassmorphismInputCard(
    value: String,
    onValueChange: (String) -> Unit,
    onMicClick: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    isMicLoading: Boolean = false
) {
    val isFocused = value.isNotEmpty()
    
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) ArameshTheme.colors.accentWood.copy(alpha = 0.5f) 
                      else ArameshTheme.colors.borderLight,
        animationSpec = tween(durationMillis = 300),
        label = "BorderColorAnimation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(ArameshTheme.colors.surfaceGlass)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor,
                        Color.Transparent,
                        borderColor.copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(24.dp)
    ) {
        Column {
            Text(
                text = "امروز چه احساسی داری؟",
                style = ArameshTheme.typography.title
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = ArameshTheme.typography.body.copy(
                    color = ArameshTheme.colors.primaryText
                ),
                cursorBrush = SolidColor(ArameshTheme.colors.accentWood),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxSize()) {
                    if (value.isEmpty()) {
                        Text(
                            text = "اینجا بنویس یا با من صحبت کن...",
                            style = ArameshTheme.typography.body
                        )
                    }
                    innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ArameshTheme.colors.accentWood.copy(alpha = 0.1f))
                        .clickable(enabled = !isMicLoading) { onMicClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isMicLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = ArameshTheme.colors.accentWood,
                            strokeWidth = 2.dp
                        )
                    } else {
                        CustomMicIcon(
                            modifier = Modifier.size(24.dp),
                            tint = ArameshTheme.colors.accentWood
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(1f)
                        .padding(start = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (value.isNotBlank()) ArameshTheme.colors.accentGreen else ArameshTheme.colors.accentGreen.copy(alpha = 0.5f))
                        .clickable(enabled = value.isNotBlank()) { onSubmit() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "تحلیل و راهکار",
                        style = ArameshTheme.typography.body.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}
