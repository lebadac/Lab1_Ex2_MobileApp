
package com.example.myapplication.ui  // Đặt package lên đầu

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.utils.GeminiAPI

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputText = findViewById<EditText>(R.id.inputText)
        val analyzeButton = findViewById<Button>(R.id.btnAnalyze)
        val resultIcon = findViewById<ImageView>(R.id.resultIcon)

        analyzeButton.setOnClickListener {
            val userInput = inputText.text.toString()
            if (userInput.isNotEmpty()) {
                callGeminiApi(userInput, resultIcon)
            }
        }
    }

    private fun callGeminiApi(input: String, resultIcon: ImageView) {
        GeminiAPI.generateResponse(this, input) { outputText, iconResId ->
            runOnUiThread {
                Log.d("MainActivity", "Icon Resource ID: $iconResId")
                if (iconResId != null) {
                    resultIcon.setImageResource(iconResId)
                    resultIcon.visibility = View.VISIBLE
                } else {
                    Log.e("MainActivity", "Icon không được cập nhật do iconResId = null")
                    resultIcon.visibility = View.GONE
                }
            }
        }
    }
}
