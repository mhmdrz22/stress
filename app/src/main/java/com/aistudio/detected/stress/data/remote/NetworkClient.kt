package com.aistudio.detected.stress.data.remote

import com.aistudio.detected.stress.data.local.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader

object NetworkClient {
    private const val API_URL = "https://ais-dev-t2u3h65li4eix7p7qd6qnm-522132295476.europe-west1.run.app/api/v1/analyze-chat"
    private const val FALLBACK_URL = "http://10.0.2.2:8000/api/v1/analyze-chat"

    suspend fun analyzeChat(deviceId: String, history: List<ChatMessage>, currentMessage: String): JSONObject? = withContext(Dispatchers.IO) {
        var result: JSONObject? = null
        try {
            result = makeRequest(API_URL, deviceId, history, currentMessage)
        } catch (e: Exception) {
            try {
                result = makeRequest(FALLBACK_URL, deviceId, history, currentMessage)
            } catch (fallbackEx: Exception) {
                fallbackEx.printStackTrace()
                null
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
                // Take last 4 messages to avoid context overflow
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
