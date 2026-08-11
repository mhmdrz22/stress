package com.aistudio.detected.stress.data.remote

import com.aistudio.detected.stress.data.local.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import android.util.Log

object NetworkClient {
    // Priority 1: Local Ollama (if deployed on same server)
    private const val OLLAMA_URL = "http://localhost:11434/api/generate"
    
    // Priority 2: DeepSeek API (cheap, good for Persian)
    private const val DEEPSEEK_URL = "https://api.deepseek.com/chat/completions"
    
    // Priority 3: HuggingFace Serverless
    private const val HF_URL = "https://api-inference.huggingface.co/models/Qwen/Qwen2.5-7B-Instruct/v1/chat/completions"
    
    private const val FALLBACK_URL = "http://10.0.2.2:8000/api/v1/analyze-chat"
    private const val MAX_RETRIES = 3

    // === OLLAMA (Local LLM - Free, No Internet needed for server) ===
    suspend fun analyzeLocalLLM(deviceId: String, history: List<ChatMessage>, currentMessage: String): JSONObject? = 
        withContext(Dispatchers.IO) {
            retryWithBackoff {
                val connection = URL(OLLAMA_URL).openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true
                    connectTimeout = 60000 // Local model needs more time
                    readTimeout = 60000
                }
                
                val prompt = buildPrompt(history, currentMessage)
                val body = JSONObject().apply {
                    put("model", "qwen2.5:7b") // or "deepseek-r1:7b"
                    put("prompt", prompt)
                    put("stream", false)
                    put("format", "json")
                }
                
                connection.outputStream.use { it.write(body.toString().toByteArray()) }
                
                if (connection.responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val json = JSONObject(response)
                    val content = json.getString("response")
                    extractJsonFromText(content)
                } else null
            }
        }

    // === HUGGINGFACE ===
    suspend fun analyzeChat(deviceId: String, history: List<ChatMessage>, currentMessage: String): JSONObject? = 
        withContext(Dispatchers.IO) {
            retryWithBackoff {
                makePostRequest(HF_URL, deviceId, history, currentMessage, hfToken = true)
            }
        }

    // === DEEPSEEK (Cheap API, good Persian support) ===
    suspend fun analyzeDeepSeek(deviceId: String, history: List<ChatMessage>, currentMessage: String): JSONObject? =
        withContext(Dispatchers.IO) {
            retryWithBackoff {
                makePostRequest(DEEPSEEK_URL, deviceId, history, currentMessage, deepSeek = true)
            }
        }

    private fun makePostRequest(
        urlString: String, 
        deviceId: String, 
        history: List<ChatMessage>, 
        currentMessage: String,
        hfToken: Boolean = false,
        deepSeek: Boolean = false
    ): JSONObject {
        val connection = URL(urlString).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.connectTimeout = 30000
            connection.readTimeout = 30000
            
            if (hfToken) {
                connection.setRequestProperty("Authorization", "Bearer ${System.getenv("HF_TOKEN") ?: ""}")
            }
            if (deepSeek) {
                connection.setRequestProperty("Authorization", "Bearer ${System.getenv("DEEPSEEK_API_KEY") ?: ""}")
            }

            val messages = JSONArray()
            messages.put(JSONObject().apply {
                put("role", "system")
                put("content", SYSTEM_PROMPT)
            })
            
            history.takeLast(3).forEach { msg ->
                messages.put(JSONObject().apply {
                    put("role", if (msg.sender == "user") "user" else "assistant")
                    put("content", msg.content.split("|||")[0])
                })
            }
            
            messages.put(JSONObject().apply {
                put("role", "user")
                put("content", currentMessage)
            })

            val body = JSONObject().apply {
                put("model", when {
                    deepSeek -> "deepseek-chat"
                    else -> "Qwen/Qwen2.5-7B-Instruct"
                })
                put("messages", messages)
                put("temperature", 0.3)
                put("max_tokens", 800)
                if (!deepSeek) put("response_format", JSONObject().apply { put("type", "json_object") })
            }

            connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
            
            if (connection.responseCode == 200) {
                val responseText = connection.inputStream.bufferedReader().readText()
                val json = JSONObject(responseText)
                val content = json.getJSONArray("choices").getJSONObject(0)
                    .getJSONObject("message").getString("content")
                return extractJsonFromText(content) ?: JSONObject(responseText)
            } else {
                throw Exception("HTTP ${connection.responseCode}")
            }
        } finally {
            connection.disconnect()
        }
    }

    private inline fun <T> retryWithBackoff(action: () -> T): T {
        var lastException: Exception? = null
        var delayMs = 1000L
        repeat(MAX_RETRIES) { attempt ->
            try {
                return action()
            } catch (e: Exception) {
                lastException = e
                Log.w("NetworkClient", "Retry $attempt failed: ${e.message}")
                if (attempt < MAX_RETRIES - 1) {
                    Thread.sleep(delayMs)
                    delayMs *= 2
                }
            }
        }
        throw lastException ?: Exception("All retries failed")
    }

    private fun extractJsonFromText(text: String): JSONObject? {
        val jsonRegex = Regex("\\{[\\s\\S]*?\\}")
        val match = jsonRegex.find(text) ?: return null
        return try {
            JSONObject(match.value)
        } catch (e: Exception) {
            null
        }
    }

    private fun buildPrompt(history: List<ChatMessage>, current: String): String {
        val historyStr = history.takeLast(3).joinToString("\n") { 
            "${if (it.sender == "user") "User" else "Assistant"}: ${it.content.split("|||")[0]}" 
        }
        return """$SYSTEM_PROMPT
        
        History:
        $historyStr
        
        User: $current
        Assistant (JSON):""".trimIndent()
    }

    private const val SYSTEM_PROMPT = """You are ArameshYar (آرامشیار), an empathetic Persian mental health AI.
Analyze the user's message and respond ONLY in JSON:
{
  "has_stress": true/false,
  "category_tag": "anxiety"/"depression"/"anger"/"sleep"/"burnout"/"joy"/"exam_stress",
  "empathy_message": "Persian empathetic response with open-ended question",
  "search_keywords": ["keyword1", "keyword2"]
}"""
}
