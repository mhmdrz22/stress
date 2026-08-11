import os
import glob

# Mapping of old strings to new strings
# We must be careful about order (longer strings first if there is overlap)
REPLACEMENTS = {
    "MainScreen": "ChatScreen",
    "StatsScreen": "DashboardScreen",
    "IntroScreen": "OnboardingScreen",
    "AdminLoginScreen": "AdminAuthScreen",
    "StressViewModel": "ChatViewModel",
    "StressIntent": "ChatIntent",
    "StressState": "ChatState",
    "LocalJsonRagEngine": "OfflineRagEngine",
    "LocalAdviceGraph": "AdviceRepository",
    "NeuroStressAnalyzer": "TFLiteClassifier",
    "NetworkClient": "BackendApiClient"
}

# Find all Kotlin files
kt_files = glob.glob("app/src/main/java/**/*.kt", recursive=True)

# 1. Replace strings in files
for filepath in kt_files:
    with open(filepath, "r", encoding="utf-8") as f:
        content = f.read()
        
    new_content = content
    for old_str, new_str in REPLACEMENTS.items():
        new_content = new_content.replace(old_str, new_str)
        
    if content != new_content:
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(new_content)
        print(f"Updated content in {filepath}")

# 2. Rename files if their names contain the old strings
for filepath in kt_files:
    filename = os.path.basename(filepath)
    new_filename = filename
    for old_str, new_str in REPLACEMENTS.items():
        if old_str in new_filename:
            new_filename = new_filename.replace(old_str, new_str)
            break # Assume one match per filename
            
    if new_filename != filename:
        new_filepath = os.path.join(os.path.dirname(filepath), new_filename)
        os.rename(filepath, new_filepath)
        print(f"Renamed {filepath} -> {new_filepath}")

print("Refactoring complete.")
