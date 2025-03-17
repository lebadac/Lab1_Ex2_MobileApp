package com.example.myapplication.ui  // Define the package at the top

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.utils.GeminiAPI

/**
 * MainActivity is the main screen of the application, allowing users to input text
 * and analyze sentiment using the Gemini API.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        val inputText = findViewById<EditText>(R.id.inputText)
        val analyzeButton = findViewById<Button>(R.id.btnAnalyze)
        val resultIcon = findViewById<ImageView>(R.id.resultIcon)

        // Set up click listener for the "Analyze" button
        analyzeButton.setOnClickListener {
            val userInput = inputText.text.toString()
            if (userInput.isNotEmpty()) {
                callGeminiApi(userInput, resultIcon) // Call the API to analyze sentiment
            }
        }
    }

    /**
     * Sends user input to the Gemini API for sentiment analysis and updates the UI.
     *
     * @param input The text input by the user.
     * @param resultIcon ImageView to display the corresponding sentiment icon.
     */
    private fun callGeminiApi(input: String, resultIcon: ImageView) {
        GeminiAPI.generateResponse(this, input) { outputText, iconResId ->
            runOnUiThread {
                Log.d("MainActivity", "Icon Resource ID: $iconResId")
                if (iconResId != null) {
                    resultIcon.setImageResource(iconResId) // Update the icon based on sentiment result
                    resultIcon.visibility = View.VISIBLE
                } else {
                    Log.e("MainActivity", "Icon not updated because iconResId = null")
                    resultIcon.visibility = View.GONE // Hide the icon if no valid result is returned
                }
            }
        }
    }
}
