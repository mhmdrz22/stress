package com.aistudio.detected.stress.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader

@Serializable
data class RagDocument(
    val text: String,
    val is_stress: Boolean,
    val category: String,
    val query: String,
    val empathy: String
)

class OfflineRagEngine(private val context: Context) {

    private val documents = mutableListOf<RagDocument>()
    private val jsonParser = Json { ignoreUnknownKeys = true }
    private var isLoaded = false

    suspend fun loadDataset() = withContext(Dispatchers.IO) {
        if (isLoaded) return@withContext
        try {
            val inputStream = context.assets.open("offline_rag_dataset.json")
            val jsonString = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
            
            val parsedDocs = jsonParser.decodeFromString<List<RagDocument>>(jsonString)
            documents.addAll(parsedDocs)
            
            isLoaded = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateSimilarity(queryWords: Set<String>, docWords: Set<String>): Double {
        val intersection = queryWords.intersect(docWords).size.toDouble()
        val union = queryWords.union(docWords).size.toDouble()
        return if (union == 0.0) 0.0 else intersection / union
    }

    suspend fun findBestMatch(userInput: String): RagDocument? = withContext(Dispatchers.Default) {
        if (!isLoaded) loadDataset()

        val queryWords = userInput.lowercase().split(Regex("\\W+")).filter { it.isNotBlank() }.toSet()
        if (queryWords.isEmpty()) return@withContext null

        val bestMatch = documents.maxByOrNull { doc ->
            val docWords = doc.text.lowercase().split(Regex("\\W+")).filter { it.isNotBlank() }.toSet()
            calculateSimilarity(queryWords, docWords)
        }

        return@withContext bestMatch
    }
}
