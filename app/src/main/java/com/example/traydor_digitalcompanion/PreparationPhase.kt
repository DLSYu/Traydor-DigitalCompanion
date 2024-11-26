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

        etNumberOfPlayers = findViewById(R.id.etNumberOfPlayers)
        tvPreparationPrompt = findViewById(R.id.tvPreparationPrompt)
        btnNextPrompt = findViewById(R.id.btnNextPrompt)

        // Initialize the prompt text visibility to invisible
        tvPreparationPrompt.text = ""
        btnNextPrompt.isEnabled = false

        // Enable the Next button only when the user enters a number of players
        etNumberOfPlayers.setOnEditorActionListener { _, _, _ ->
            val numOfPlayers = etNumberOfPlayers.text.toString().toIntOrNull()
            if (numOfPlayers != null && numOfPlayers >= 5 && numOfPlayers <= 10) {
                // Set the player count in GameController
                GameController.setInitialPlayerCount(numOfPlayers)
                Log.d("PreparationPhase", "Number of players set: $numOfPlayers")

                // Hide the EditText and show the prompts
                etNumberOfPlayers.visibility = View.GONE
                tvPreparationPrompt.text = prompts[currentPromptIndex]
                btnNextPrompt.isEnabled = true
            } else {
                // Notify user that input is invalid
                etNumberOfPlayers.error = "Please enter a valid number of players (5-10)."
            }
            false
        }

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
