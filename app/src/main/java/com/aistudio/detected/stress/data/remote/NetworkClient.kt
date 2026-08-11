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
    private const val API_URL = "https://ais-dev-t2u3h65li4eix7p7qd6qnm-522132295476.europe-west1.run.app/api/v1/analyze-chat"
    private const val FALLBACK_URL = "http://10.0.2.2:8000/api/v1/analyze-chat"
    private const val MAX_RETRIES = 3

    suspend fun analyzeChat(deviceId: String, history: List<ChatMessage>, currentMessage: String): JSONObject? = withContext(Dispatchers.IO) {
        var result: JSONObject? = null
        var attempt = 0
        var currentDelay = 1000L

        while (attempt < MAX_RETRIES && result == null) {
            try {
                result = makeRequest(API_URL, deviceId, history, currentMessage)
            } catch (e: Exception) {
                Log.e("NetworkClient", "Attempt ${attempt + 1} failed: ${e.message}")
                attempt++
                if (attempt < MAX_RETRIES) {
                    delay(currentDelay)
                    currentDelay *= 2
                }
            }
        }
        
        if (result == null) {
            try {
                Log.d("NetworkClient", "Trying fallback URL")
                result = makeRequest(FALLBACK_URL, deviceId, history, currentMessage)
            } catch (fallbackEx: Exception) {
                Log.e("NetworkClient", "Fallback failed: ${fallbackEx.message}")
            }
        }
        
        result
    }

    private fun makeRequest(urlString: String, deviceId: String, history: List<ChatMessage>, currentMessage: String): JSONObject {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            val jsonBody = JSONObject().apply {
                put("device_id", deviceId)
                put("current_message", currentMessage)
                
                val historyArray = JSONArray()
                history.takeLast(4).forEach { msg ->
                    val msgObj = JSONObject()
                    msgObj.put("role", msg.sender)
                    msgObj.put("text", msg.content.split("|||")[0])
                    historyArray.put(msgObj)
                }
                put("history", historyArray)
            }

            connection.outputStream.use { os ->
                val input = jsonBody.toString().toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val responseString = reader.readText()
                return JSONObject(responseString)
            } else {
                throw Exception("HTTP error code: $responseCode")
            }
        } finally {
            connection.disconnect()
        }
    }
}
