import sys

with open("app/src/main/java/com/aistudio/detected/stress/ui/screens/MainScreen.kt", "r") as f:
    content = f.read()

# Add import
if "import com.aistudio.detected.stress.ui.components.VideoSuggestionsCarousel" not in content:
    content = content.replace("import com.aistudio.detected.stress.ui.components.GlassmorphismInputCard", "import com.aistudio.detected.stress.ui.components.GlassmorphismInputCard\nimport com.aistudio.detected.stress.ui.components.VideoSuggestionsCarousel")

# Replace Video Search Suggestions
target = """                                // Video Search Suggestions
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
                                }"""

replacement = """                                // Video Search Suggestions (Carousel)
                                if (data.search_keywords.isNotEmpty()) {
                                    Text(
                                        text = "محتوای ویدیویی پیشنهادی",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    VideoSuggestionsCarousel(queries = data.search_keywords)
                                    Spacer(modifier = Modifier.height(24.dp))
                                }"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/aistudio/detected/stress/ui/screens/MainScreen.kt", "w") as f:
    f.write(content)

