# Stress Assessment Safety Specification

## Purpose
This app provides a non-diagnostic, self-reported stress check-in and low-risk wellbeing guidance. It must not claim to diagnose a medical or mental-health condition.

## Assessment model
Use a short, configurable questionnaire with 0-4 answers (Never to Very often). Store the question version with each completed assessment. Calculate the total only from answered items and require all required answers before returning a level.

## Result contract
Return a structured result rather than only free text:

```kotlin
enum class StressLevel { LOW, MODERATE, HIGH, URGENT }

data class StressAssessmentResult(
    val totalScore: Int,
    val maxScore: Int,
    val level: StressLevel,
    val assessmentVersion: String,
    val completedAtEpochMillis: Long,
    val disclaimer: String,
    val recommendedActions: List<String>,
    val shouldEscalate: Boolean
)
```

Do not use an LLM to calculate scores, select an emergency level, or override deterministic safety rules. An LLM may only personalize wording after the structured result is produced.

## Safety gate
Ask an optional, direct safety question before generating advice. If the user indicates immediate danger, thoughts of self-harm, harm to others, or inability to stay safe, return `URGENT`. Do not continue ordinary coaching. Show a short, localized emergency screen that tells the user to contact local emergency services, a crisis service where available, or a trusted person nearby. If location is unavailable, do not guess a phone number.

`URGENT` must take precedence over questionnaire scoring. Persist only an opt-in, privacy-safe event; never save raw crisis text by default.

## Guidance rules
- LOW: brief prevention actions, such as sleep routine, movement, and a short breathing exercise.
- MODERATE: 1-3 actionable steps, a check-in reminder, and an option to talk with a health professional if symptoms persist or interfere with daily life.
- HIGH: supportive language, immediate grounding options, encouragement to contact a licensed professional or trusted person, and a short follow-up interval.
- URGENT: emergency guidance only; do not provide a long AI-generated plan.

Never promise outcomes, provide medication instructions, or present generated text as professional medical advice.

## Privacy and security
- Keep answers on-device by default.
- Obtain explicit consent before cloud processing or analytics.
- Minimize retained data and offer deletion/export controls.
- Do not log prompts, answers, identifiers, or API keys.
- Keep credentials outside the repository and scan changes before publishing.

## Quality gates
Add unit tests for score boundaries, incomplete answers, the urgent override, and result serialization. Add UI tests that verify an urgent result blocks normal coaching and exposes emergency guidance.

## Acceptance criteria
1. A completed assessment always returns a deterministic structured result.
2. Urgent safety signals override every non-urgent result.
3. Advice is proportional to level and never framed as diagnosis.
4. The user can understand data use and delete stored assessments.
5. Critical score and safety paths are covered by automated tests.
