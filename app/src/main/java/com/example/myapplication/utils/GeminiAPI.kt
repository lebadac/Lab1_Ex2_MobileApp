package com.example.myapplication.utils

import android.content.Context
import android.util.Log
import com.example.myapplication.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson
import java.io.IOException

object GeminiAPI {
    private const val API_KEY = "AIzaSyCpAeJv7kQipl8IiGTsUSIlFJ2wBRMtL2Y"  // Thay API key thực tế của bạn
    private const val GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$API_KEY"

    private val client = OkHttpClient()
    private val gson = Gson()

    fun generateResponse(context: Context, prompt: String, callback: (String?, Int?) -> Unit) {
        val jsonBody = """
        {
            "contents": [
                {
                    "parts": [
                        {
                            "text": "Determine the sentiment of the following sentence: '$prompt'. The response should be only either 'Positive', 'Negative', or 'Neutral'"
                        }
                    ]
                }
            ]
        }
        """.trimIndent()

        val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(GEMINI_URL)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("GeminiAPI", "API Call Failed: ${e.message}")
                callback("Error: ${e.message}", null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { jsonResponse ->
                    Log.d("GeminiAPI", "API Response: $jsonResponse")
                    val parsedResponse = gson.fromJson(jsonResponse, GeminiResponse::class.java)
                    val outputText = parsedResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                    Log.d("GeminiAPI", "Parsed Output: $outputText")
                    val sentimentIconId: Int? = when (outputText) {
                        "Positive" -> R.drawable.ic_happy
                        "Negative" -> R.drawable.ic_sad
                        "Neutral" -> R.drawable.ic_neutral
                        else -> null
                    }
                    Log.d("GeminiAPI", "Selected Icon ID: $sentimentIconId")
                    callback(outputText, sentimentIconId)
                } ?: run {
                    callback("Error: Failed to parse the response from the API", null)
                }
            }
        })
    }

    data class GeminiResponse(
        val candidates: List<Candidate>
    ) {
        data class Candidate(
            val content: Content
        )

        data class Content(
            val parts: List<Part>
        )

        data class Part(
            val text: String
        )
    }
}
