package com.example.traydor_digitalcompanion

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class NightPhaseActivity : AppCompatActivity() {

    private lateinit var imgvwNightPrompt: ImageView
    private lateinit var tvNightPhasePrompt: TextView
    private lateinit var btnNightPhase: Button

    // Role actions list following the specified sequence: Traydor, Heneral, Espiya, Arrepentida
    private val roleActions = listOf(
        //TODO: replace image to correct version of role card
        Pair(R.drawable.mabini, "Traydor, you are allowed to open your eyes during the Night Phase. Be careful not to get caught."),
        Pair(R.drawable.mabini,"Heneral takes action."),
        Pair(R.drawable.mabini, "Espiya collects information."),
        Pair(R.drawable.mabini, "Arrepentida uses ability.")
    )
    private var currentRoleActionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_night_phase)

        // Initialize views
        imgvwNightPrompt = findViewById(R.id.imgvwNightPrompt)
        tvNightPhasePrompt = findViewById(R.id.tvNightPhasePrompt)
        btnNightPhase = findViewById(R.id.btnNightPhase)

        // Initialize prompt for the first role action or reshuffling prompt if needed
        if (GameController.shouldShowReshufflePrompt()) {
            tvNightPhasePrompt.text = "Please reshuffle your cards before the night phase begins."
        } else {
            tvNightPhasePrompt.text = "Night has fallen. Each role will now take their actions."
        }

        btnNightPhase.text = "Do Action"

        btnNightPhase.setOnClickListener{
            when(btnNightPhase.text){
                "Do Action" -> {
                    Log.d("NightPhaseActivity", "Button clicked. Current role action index: $currentRoleActionIndex")

                    if (currentRoleActionIndex < roleActions.size) {
                        Log.d("NightPhaseActivity", "Attempting to display role action: ${roleActions[currentRoleActionIndex].second}")

                        tvNightPhasePrompt.text = roleActions[currentRoleActionIndex].second
                        imgvwNightPrompt.setImageResource(roleActions[currentRoleActionIndex].first)
                        Log.d("NightPhaseActivity", "Displayed role action prompt: ${tvNightPhasePrompt.text}")


                        currentRoleActionIndex++
                        Log.d("NightPhaseActivity", "Incremented role action index to: $currentRoleActionIndex")

                        if (currentRoleActionIndex == roleActions.size) {
                            Log.d("NightPhaseActivity", "All roles have acted. Adding delay before proceeding to folklore event.")

                            Handler(Looper.getMainLooper()).postDelayed({
                                Log.d("NightPhaseActivity", "Proceeding to folklore event after delay.")
                                checkForFolkloreEvent()
                            }, 1000) // 1-second delay before folklore event
                        }
                    }
                }

                "Next Phase" -> {
                    val intent = Intent(this, DayPhaseActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                "Press Quickly!" -> {
                    tvNightPhasePrompt.text = "Tiyanak has been appeased! The discussion time remains normal."
                    btnNightPhase.text = "Next Phase"
                }
            }
        }
    }
    // Method to check if a folklore event occurs by random chance
    private fun checkForFolkloreEvent() {
        val folkloreEvents = listOf(
            "Tikbalang", "Duwende", "Diwata", "Kura Paroko",
            "Kapre", "Tiyanak", "Manananggal"
        )
        val selectedEvent = folkloreEvents.random()

        when (selectedEvent) {
            "Tikbalang" -> tvNightPhasePrompt.text = "Tikbalang appears! Some players are silenced during the next daytime discussion."
            "Duwende" -> tvNightPhasePrompt.text = "Duwende is mischievous! Player cards are swapped."
            "Diwata" -> tvNightPhasePrompt.text = "Diwata appears! All players can look at their cards before the discussion."
            "Kura Paroko" -> tvNightPhasePrompt.text = "Kura Paroko appears! It's a peaceful night with no special effects."
            "Kapre" -> tvNightPhasePrompt.text = "Kapre appears! The roles are switched. Traydor, choose who you think is Mabini. Mabini, lay low!"
            "Tiyanak" -> {
                tvNightPhasePrompt.text = "Tiyanak appears! Press the button before time runs out, or the discussion time will be shortened."
                btnNightPhase.text = "Press Quickly!"

                Handler().postDelayed({
                    if (btnNightPhase.text == "Press Quickly!") {
                        tvNightPhasePrompt.text = "Tiyanak was not appeased in time! Discussion time is now shorter."
                        btnNightPhase.text = "Next Phase"
                    }
                }, 5000) // 5 seconds to press the button
            }
            "Manananggal" -> tvNightPhasePrompt.text = "Manananggal appears! Everyone can vote this round, not just the Mabini. The majority wins."
        }

        if (selectedEvent != "Tiyanak") {
            btnNightPhase.text = "Next Phase"
        }
    }
}
