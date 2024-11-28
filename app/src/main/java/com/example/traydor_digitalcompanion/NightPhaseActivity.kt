package com.example.traydor_digitalcompanion

import android.content.Intent
import android.media.MediaPlayer
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
    private var mediaPlayer: MediaPlayer? = null

    // Role actions list following the specified sequence: Traydor, Heneral, Espiya, Arrepentida
    private val roleActions = listOf(
        Pair(R.drawable.mabini, "Traydor, you are allowed to open your eyes during the Night Phase. Be careful not to get caught."),
        Pair(R.drawable.mabini, "Heneral takes action."),
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
            // Reshuffle prompt (not final round)
            tvNightPhasePrompt.text = "Please reshuffle your cards before the night phase begins."
            mediaPlayer = MediaPlayer.create(this, R.raw.greater_5_line)
            mediaPlayer?.start()
        } else if (GameController.isGameEnd()) {
            // Final round prompt (if 5 players remain)
            tvNightPhasePrompt.text = "This is the final round. Prepare yourselves!"
            mediaPlayer = MediaPlayer.create(this, R.raw.next_round_5)
            mediaPlayer?.start()
        } else {
            // Normal night phase start
            tvNightPhasePrompt.text = "Night has fallen. Each role will now take their actions."
            mediaPlayer = MediaPlayer.create(this, R.raw.night_on_start)
            mediaPlayer?.start()
        }

        btnNightPhase.text = "Do Action"

        btnNightPhase.setOnClickListener {
            when (btnNightPhase.text) {
                "Do Action" -> {
                    Log.d("NightPhaseActivity", "Button clicked. Current role action index: $currentRoleActionIndex")

                    if (currentRoleActionIndex < roleActions.size) {
                        Log.d("NightPhaseActivity", "Attempting to display role action: ${roleActions[currentRoleActionIndex].second}")

                        tvNightPhasePrompt.text = roleActions[currentRoleActionIndex].second
                        imgvwNightPrompt.setImageResource(roleActions[currentRoleActionIndex].first)
                        Log.d("NightPhaseActivity", "Displayed role action prompt: ${tvNightPhasePrompt.text}")

                        // Stop any ongoing audio when showing Traydor prompt
                        if (currentRoleActionIndex == 0) {
                            mediaPlayer?.stop()
                        }

                        // Play role-specific audio if applicable (excluding Traydor)
                        when (currentRoleActionIndex) {
                            1 -> mediaPlayer = MediaPlayer.create(this, R.raw.heneral_line)
                            2 -> mediaPlayer = MediaPlayer.create(this, R.raw.espiya_line)
                            3 -> mediaPlayer = MediaPlayer.create(this, R.raw.arrepentida_line)
                        }
                        mediaPlayer?.start()

                        currentRoleActionIndex++
                        Log.d("NightPhaseActivity", "Incremented role action index to: $currentRoleActionIndex")

                        if (currentRoleActionIndex == roleActions.size) {
                            Log.d("NightPhaseActivity", "All roles have acted. Adding delay before proceeding to folklore event.")

                            Handler(Looper.getMainLooper()).postDelayed({
                                Log.d("NightPhaseActivity", "Proceeding to folklore event after delay.")
                                checkForFolkloreEvent()
                            }, 4500) // 4.5-second delay before folklore event and no audio overlap
                        }
                    }
                }

                "Next Phase" -> {
                    mediaPlayer?.release()
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

        // Play folklore event start audio
        mediaPlayer = MediaPlayer.create(this, R.raw.folklore_on_start)
        mediaPlayer?.start()

        Handler(Looper.getMainLooper()).postDelayed({
            when (selectedEvent) {
                "Tikbalang" -> {
                    tvNightPhasePrompt.text = "Tikbalang appears! Some players are silenced during the next daytime discussion."
                    mediaPlayer = MediaPlayer.create(this, R.raw.tikbalang_line)
                }
                "Duwende" -> {
                    tvNightPhasePrompt.text = "Duwende is mischievous! Player cards are swapped."
                    mediaPlayer = MediaPlayer.create(this, R.raw.duwende_line)
                }
                "Diwata" -> {
                    tvNightPhasePrompt.text = "Diwata appears! All players can look at their cards before the discussion."
                    mediaPlayer = MediaPlayer.create(this, R.raw.diwata_line)
                }
                "Kura Paroko" -> {
                    tvNightPhasePrompt.text = "Kura Paroko appears! It's a peaceful night with no special effects."
                    mediaPlayer = MediaPlayer.create(this, R.raw.cura_parroco_line)
                }
                "Kapre" -> {
                    tvNightPhasePrompt.text = "Kapre appears! The roles are switched. Traydor, choose who you think is Mabini. Mabini, lay low!"
                    mediaPlayer = MediaPlayer.create(this, R.raw.kapre_line)
                }
                "Tiyanak" -> {
                    tvNightPhasePrompt.text = "Tiyanak appears! Press the button before time runs out, or the discussion time will be shortened."
                    btnNightPhase.text = "Press Quickly!"
                    mediaPlayer = MediaPlayer.create(this, R.raw.tiyanak_line)

                    Handler().postDelayed({
                        if (btnNightPhase.text == "Press Quickly!") {
                            tvNightPhasePrompt.text = "Tiyanak was not appeased in time! Discussion time is now shorter."
                            btnNightPhase.text = "Next Phase"
                        }
                    }, 10000) // 10 seconds to press the button because of audio
                }
                "Manananggal" -> {
                    tvNightPhasePrompt.text = "Manananggal appears! Everyone can vote this round, not just the Mabini. The majority wins."
                    mediaPlayer = MediaPlayer.create(this, R.raw.mananangal_line)
                }
            }
            mediaPlayer?.start()
        }, 8000) // 8-second delay between folklore start audio and event audio

        if (selectedEvent != "Tiyanak") {
            btnNightPhase.text = "Next Phase"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
