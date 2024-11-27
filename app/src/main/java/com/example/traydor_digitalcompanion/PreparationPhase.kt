package com.example.traydor_digitalcompanion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

class PreparationPhase : AppCompatActivity() {

    private lateinit var etNumberOfPlayers: EditText
    private lateinit var tvPreparationPrompt: TextView
    private lateinit var btnNextPrompt: Button
    private val prompts = listOf(
        "Please shuffle and distribute the role cards.",
        "Please organize the physical components of the game (cards, players, protection token, and digital companion device).",
        "Please check your Role Card."
    )
    private var currentPromptIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.preparation_phase)

        tvPreparationPrompt = findViewById(R.id.tvPreparationPrompt)
        btnNextPrompt = findViewById(R.id.btnNextPrompt)

        //Initialize prompts for Preparation Stage
        tvPreparationPrompt.text = prompts[currentPromptIndex]
        btnNextPrompt.isEnabled = true

        // Set up the button to navigate through prompts
        btnNextPrompt.setOnClickListener {
            currentPromptIndex++
            if (currentPromptIndex < prompts.size) {
                tvPreparationPrompt.text = prompts[currentPromptIndex]
            } else {
                // Transition to the Night Phase
                val intent = Intent(this, NightPhaseActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
