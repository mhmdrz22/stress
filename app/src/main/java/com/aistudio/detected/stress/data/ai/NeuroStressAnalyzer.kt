package com.aistudio.detected.stress.data.ai

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.json.JSONObject
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import android.util.Log

class NeuroStressAnalyzer(private val context: Context) {

    private var tflite: Interpreter? = null
    private val vocabMap = mutableMapOf<String, Int>()
    private val MAX_SEQUENCE_LENGTH = 100

    init {
        try {
            val fileDescriptor = context.assets.openFd("stress_model_quantized.tflite")
            if (fileDescriptor.declaredLength > 0) {
                val options = Interpreter.Options().apply { numThreads = 2 }
                tflite = Interpreter(loadModelFile(), options)
                loadVocab()
            } else {
                Log.w("NeuroStressAnalyzer", "TFLite model is empty. Running in fallback mode.")
            }
        } catch (e: Exception) {
            Log.w("NeuroStressAnalyzer", "TFLite model not loaded. Running in fallback mode.")
            tflite = null
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("stress_model_quantized.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }

    private fun loadVocab() {
        val jsonString = context.assets.open("vocab.json").bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        jsonObject.keys().forEach { key ->
            vocabMap[key] = jsonObject.getInt(key)
        }
    }

    private fun tokenize(text: String): FloatArray {
        val words = text.lowercase().split(Regex("\\W+")).filter { it.isNotBlank() }
        val tokenized = FloatArray(MAX_SEQUENCE_LENGTH)
        
        for (i in 0 until minOf(words.size, MAX_SEQUENCE_LENGTH)) {
            tokenized[i] = vocabMap[words[i]]?.toFloat() ?: 0f
        }
        return tokenized
    }

    fun predictStressCategory(userInput: String): String {
        if (tflite == null) {
            return "anxiety" 
        }

        try {
            val inputTensor = arrayOf(tokenize(userInput))
            val outputTensor = arrayOf(FloatArray(1)) 
            
            tflite?.run(inputTensor, outputTensor)

            val stressProbability = outputTensor[0][0]
            
            return if (stressProbability > 0.5f) {
                "anxiety"
            } else {
                "joy"
            }
        } catch (e: Exception) {
            Log.e("NeuroStressAnalyzer", "Prediction failed", e)
            return "anxiety"
        }
    }
}
