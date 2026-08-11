import re

with open("app/src/main/java/com/aistudio/detected/stress/ui/screens/MainScreen.kt", "r") as f:
    content = f.read()

old_code = """(fadeIn(tween(500)) + scaleIn(initialScale = 0.9f)) togetherWith 
                        (fadeOut(tween(300)) + scaleOut(targetScale = 0.9f)).using(SizeTransform(clip = false))"""

new_code = """((fadeIn(tween(500)) + scaleIn(initialScale = 0.9f)) togetherWith 
                        (fadeOut(tween(300)) + scaleOut(targetScale = 0.9f))).using(SizeTransform(clip = false))"""

content = content.replace(old_code, new_code)

with open("app/src/main/java/com/aistudio/detected/stress/ui/screens/MainScreen.kt", "w") as f:
    f.write(content)

