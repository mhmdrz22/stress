package com.aistudio.detected.stress.agents

import com.aistudio.detected.stress.data.AdviceItem
import com.aistudio.detected.stress.data.LocalAdviceGraph
import com.aistudio.detected.stress.data.local.ChatMessage

object AdviceGraphAgent {
    
    fun getAdvice(
        category: String, 
        likedTitles: List<String> = emptyList(),
        history: List<ChatMessage> = emptyList()
    ): AdviceGraphResult {
        
        // Extract already-shown advice from chat history
        val shownAdvice = history.flatMap { msg ->
            msg.content.split("|||").flatMap { part ->
                LocalAdviceGraph.adviceList.map { it.title }.filter { part.contains(it) }
            }
        }.toSet()
        
        // Get all advice for category
        val allAdvice = LocalAdviceGraph.getAdviceForCategory(category, likedTitles)
        
        // Deduplicate: remove already shown, limit to 4
        val freshAdvice = allAdvice
            .filter { it.title !in shownAdvice }
            .take(4)
            .ifEmpty { allAdvice.shuffled().take(2) } // If all shown, show random 2
        
        // Dynamic keywords based on category + user history
        val keywords = generateDynamicKeywords(category, history)
        
        return AdviceGraphResult(freshAdvice, keywords)
    }
    
    private fun generateDynamicKeywords(
        category: String, 
        history: List<ChatMessage>
    ): List<String> {
        val baseKeywords = when (category) {
            "anger" -> listOf("تکنیک رهاسازی خشم", "مدیتیشن آرامش اعصاب")
            "sleep" -> listOf("موسیقی خواب عمیق دلتا", "تکنیک خواب ۴-۷-۸")
            "burnout" -> listOf("مدیتیشن بازیابی انرژی", "تکنیک پومودورو")
            "depression" -> listOf("پادکست انگیزشی صبحگاهی", "تمرین شکرگزاری")
            "anxiety" -> listOf("تکنیک تنفس مربع اضطراب", "مدیتیشن ذهنآگاهی")
            "exam_stress" -> listOf("تکنیک تمرکز قبل امتحان", "مدیتیشن کاهش استرس امتحان")
            "joy" -> listOf("موسیقی فرکانس مثبت", "تمرین گسترش خوشحالی")
            else -> listOf("مدیتیشن آرامش", "تکنیک تنفس")
        }
        
        // Personalize: if user previously clicked on sleep videos, add more sleep
        val userPreferences = history.flatMap { it.content.split(",") }
            .groupingBy { it }.eachCount()
            .toList().sortedByDescending { it.second }.take(2).map { it.first }
        
        return (baseKeywords + userPreferences).distinct().take(3)
    }
}

data class AdviceGraphResult(
    val adviceList: List<AdviceItem>,
    val searchKeywords: List<String>
)
