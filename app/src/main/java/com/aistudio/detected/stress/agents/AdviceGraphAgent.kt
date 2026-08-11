package com.aistudio.detected.stress.agents

import com.aistudio.detected.stress.data.AdviceItem
import com.aistudio.detected.stress.data.LocalAdviceGraph

object AdviceGraphAgent {
    /**
     * Agent 3: Advice Graph Agent (عامل مشاور)
     * Selects actionable tips and video links from a static local graph.
     */
    fun getAdvice(category: String, likedTitles: Set<String>): AdviceGraphResult {
        val adviceItems = LocalAdviceGraph.getAdviceForCategory(category, likedTitles.toList())
        
        // Generate general search keywords
        val keywords = when (category) {
            "anger" -> listOf("کنترل خشم", "مدیتیشن آرامش")
            "sleep" -> listOf("موسیقی خواب", "مدیتیشن خواب عمیق")
            "burnout" -> listOf("رفع خستگی", "انگیزه دوباره")
            "depression" -> listOf("امیدواری", "غلبه بر افسردگی")
            "anxiety" -> listOf("کاهش استرس", "تکنیک تنفس")
            else -> listOf("انرژی مثبت", "موفقیت")
        }
        
        return AdviceGraphResult(adviceItems, keywords)
    }
}

data class AdviceGraphResult(
    val adviceList: List<AdviceItem>,
    val searchKeywords: List<String>
)
