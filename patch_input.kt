import androidx.compose.material3.CircularProgressIndicator

// in GlassmorphismInputCard:
@Composable
fun GlassmorphismInputCard(
    value: String,
    onValueChange: (String) -> Unit,
    onMicClick: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    isMicLoading: Boolean = false
) {
// ...
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
