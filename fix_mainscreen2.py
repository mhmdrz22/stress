import re

with open('app/src/main/java/com/aistudio/detected/stress/ui/screens/MainScreen.kt', 'r', encoding='utf-8') as f:
    content = f.read()

# Add imports
content = content.replace("import com.aistudio.detected.stress.ui.components.GlassmorphismInputCard", 
                          "import com.aistudio.detected.stress.ui.components.GlassmorphismInputCard\nimport com.aistudio.detected.stress.ui.components.BreathingExercise\nimport com.aistudio.detected.stress.ui.components.OfflineAudioPlayer")

# Replace the search keywords section
old_search_block = """                                    data.search_keywords.forEach { query ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(query)}"))
                                                    context.startActivity(intent)
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(48.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                                                shape = RoundedCornerShape(12.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFFEF4444))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("یوتیوب", color = Color(0xFFB91C1C), style = ArameshTheme.typography.label)
                                            }

                                            Button(
                                                onClick = {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.aparat.com/search/${Uri.encode(query)}"))
                                                    context.startActivity(intent)
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(48.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                                shape = RoundedCornerShape(12.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Icon(Icons.Default.Search, contentDescription = null, tint = ArameshTheme.colors.accentWood)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("آپارات", color = ArameshTheme.colors.accentWood, style = ArameshTheme.typography.label)
                                            }
                                        }
                                    }"""

new_search_block = """                                    if (state.isOffline) {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Text(
                                            text = "جعبه‌کمک‌های اولیه (آفلاین)",
                                            style = ArameshTheme.typography.title.copy(fontSize = 18.sp),
                                            color = ArameshTheme.colors.primaryText
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))

                                        // ۱. نمایش ابزار تنفس آفلاین
                                        BreathingExercise()

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // ۲. پخش موسیقی آرامش‌بخش آفلاین (بدون نیاز به دانلود یا فیلترشکن)
                                        OfflineAudioPlayer(
                                            title = "صدای باران و طبیعت (بدون نیاز به اینترنت)"
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))
                                    }

                                    data.search_keywords.forEach { query ->
                                        if (state.isOffline) {
                                            Button(
                                                onClick = {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.aparat.com/search/${Uri.encode(query)}"))
                                                    context.startActivity(intent)
                                                },
                                                modifier = Modifier.fillMaxWidth().height(48.dp).padding(bottom = 8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3E8FF)),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text("جستجوی ویدیوی مرتبط در آپارات", color = Color(0xFF7E22CE))
                                            }
                                        } else {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 12.dp),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Button(
                                                    onClick = {
                                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(query)}"))
                                                        context.startActivity(intent)
                                                    },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(48.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                                                    shape = RoundedCornerShape(12.dp),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFFEF4444))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("یوتیوب", color = Color(0xFFB91C1C), style = ArameshTheme.typography.label)
                                                }

                                                Button(
                                                    onClick = {
                                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.aparat.com/search/${Uri.encode(query)}"))
                                                        context.startActivity(intent)
                                                    },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(48.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                                    shape = RoundedCornerShape(12.dp),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Icon(Icons.Default.Search, contentDescription = null, tint = ArameshTheme.colors.accentWood)
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("آپارات", color = ArameshTheme.colors.accentWood, style = ArameshTheme.typography.label)
                                                }
                                            }
                                        }
                                    }"""

content = content.replace(old_search_block, new_search_block)

with open('app/src/main/java/com/aistudio/detected/stress/ui/screens/MainScreen.kt', 'w', encoding='utf-8') as f:
    f.write(content)

