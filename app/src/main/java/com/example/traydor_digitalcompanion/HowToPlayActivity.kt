package com.example.traydor_digitalcompanion

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HowToPlayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_play)

        val btnBack: Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Close this activity and go back to MainActivity
        }
    }
}

