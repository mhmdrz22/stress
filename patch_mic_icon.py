import re

with open("app/src/main/java/com/aistudio/detected/stress/ui/components/GlassmorphismInputCard.kt", "r") as f:
    content = f.read()

old_code = """                    CustomMicIcon(
                        modifier = Modifier.size(24.dp),
                        tint = ArameshTheme.colors.accentWood
                    )"""

new_code = """                    if (isMicLoading) {
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
                    }"""

content = content.replace(old_code, new_code)

with open("app/src/main/java/com/aistudio/detected/stress/ui/components/GlassmorphismInputCard.kt", "w") as f:
    f.write(content)

