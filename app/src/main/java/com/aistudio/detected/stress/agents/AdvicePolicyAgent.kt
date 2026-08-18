package com.aistudio.detected.stress.agents

import com.aistudio.detected.stress.data.AdviceItem
import com.aistudio.detected.stress.data.StressLevel

data class AdvicePolicyRequest(
    val category: String,
    val stressLevel: StressLevel?,
    val isCrisis: Boolean,
    val previouslyShownTitles: Set<String> = emptySet(),
    val likedTitles: Set<String> = emptySet()
)

data class AdvicePolicyResult(
    val adviceList: List<AdviceItem>,
    val searchKeywords: List<String>
)

/**
 * Applies deterministic safety and relevance rules before existing local advice is shown.
 */
object AdvicePolicyAgent {
    fun select(request: AdvicePolicyRequest): AdvicePolicyResult {
        if (request.isCrisis || request.stressLevel == StressLevel.URGENT) {
            return AdvicePolicyResult(emptyList(), emptyList())
        }

        val base = AdviceGraphAgent.getAdvice(
            category = request.category,
            likedTitles = request.likedTitles.toList(),
            history = emptyList()
        )

        val maxItems = when (request.stressLevel) {
            StressLevel.HIGH -> 2
            StressLevel.MODERATE -> 3
            StressLevel.LOW, null -> 3
            StressLevel.URGENT -> 0
        }

        val freshAdvice = base.adviceList
            .filterNot { it.title in request.previouslyShownTitles }
            .take(maxItems)
            .ifEmpty { base.adviceList.take(maxItems) }

        val keywords = if (freshAdvice.isEmpty()) {
            emptyList()
        } else {
            base.searchKeywords.take(3)
        }

        return AdvicePolicyResult(freshAdvice, keywords)
    }
}
