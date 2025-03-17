package com.example.myapplication.utils

import android.content.Context
import android.util.Log
import com.example.myapplication.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson
import java.io.IOException

/**
 * GeminiAPI is a utility object that interacts with Google's Gemini API
 * to analyze the sentiment of a given text input.
 */
object GeminiAPI {
    private val client = OkHttpClient()
    private val gson = Gson()

    /**
     * Sends a request to the Gemini API to determine the sentiment of a given text.
     *
     * @param context The application context.
     * @param prompt The input text whose sentiment needs to be analyzed.
     * @param callback A function that receives the sentiment result and corresponding icon resource ID.
     */
    fun generateResponse(context: Context, prompt: String, callback: (String?, Int?) -> Unit) {
        val apiKey = context.getString(R.string.gemini_api_key) // Fetch API key from strings.xml
        val geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"

        // JSON body to send to Gemini API
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
            .url(geminiUrl) // Using predefined URL with API key
            .post(requestBody)
            .build()

        // Execute the API call asynchronously
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("GeminiAPI", "API Call Failed: ${e.message}")
                callback("Error: ${e.message}", null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { jsonResponse ->
                    Log.d("GeminiAPI", "API Response: $jsonResponse")
                    // Parse API response
                    val parsedResponse = gson.fromJson(jsonResponse, GeminiResponse::class.java)
                    val outputText = parsedResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                    Log.d("GeminiAPI", "Parsed Output: $outputText")

                    // Map sentiment result to corresponding icon
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

    /**
     * Data classes representing the structure of the API response.
     */
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

