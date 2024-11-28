package com.example.traydor_digitalcompanion

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Visibility
import android.os.Handler

class DayPhaseActivity : AppCompatActivity() {

    private lateinit var imgvwDayPrompt: ImageView
    private lateinit var tvDayPhasePrompt: TextView
    private lateinit var tvTimer: TextView
    private lateinit var btnDayPhase: Button
    private var mediaPlayer: MediaPlayer? = null

    private var isTiyanakEventActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_day_phase)

        // Initialize views
        imgvwDayPrompt = findViewById(R.id.imgvwDayPrompt)
        tvDayPhasePrompt = findViewById(R.id.tvDayPhasePrompt)
        tvTimer = findViewById(R.id.tvTimer)
        btnDayPhase = findViewById(R.id.btnDayPhase)

        // Play day phase start audio
        mediaPlayer = MediaPlayer.create(this, R.raw.morning_on_start)
        mediaPlayer?.start()

        // Add delay before starting the discussion timer
        Handler().postDelayed({
            // Determine if the Tiyanak folklore event was active from the Night Phase
            isTiyanakEventActive = intent.getBooleanExtra("TiyanakEventActive", false)

            // Initialize prompt for the Day Phase
            tvDayPhasePrompt.text = "Day has started. Players will now discuss and act."
            imgvwDayPrompt.setImageResource(R.drawable.sun)

            // Hide Next Phase button initially
            btnDayPhase.text = "Skip Timer"

            // Initialize the timer based on the Tiyanak event
            startDiscussionTimer(isTiyanakEventActive)
        }, 4500) // 4.5-second delay before starting the timer


        btnDayPhase.setOnClickListener{
            when(btnDayPhase.text){
                "Skip Timer" -> {
                    tvTimer.text = "00:00"
                    tvDayPhasePrompt.text = "Discussion time is over. Mabini will now decide who to vote."
                    //TODO: replace image to correct version of role card
                    imgvwDayPrompt.setImageResource(R.drawable.mabini)
                    playMorningOnEndAudio()
                    btnDayPhase.text = "Next Phase"
                }

                "Next Phase" -> {
                    GameController.playerVotedOut()

                    if (GameController.isFinalRound()) {
                        Log.d("DayPhaseActivity", "Final round reached, transitioning to the last night phase.")
                        GameController.markFinalRoundPlayed()

                        // Proceed to the last night phase
                        val intent = Intent(this, NightPhaseActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else if (GameController.isGameEnd()) {
                        Log.d("DayPhaseActivity", "Game ending. Showing final results.")
                        tvTimer.visibility = View.GONE
                        tvDayPhasePrompt.text = "The game has ended! Determining the final victors..."
                        btnDayPhase.text = "End Game"
                        GameController.resetGameValues() // Reset game values for a new game

                        // Set button click to go back to the main menu
                        btnDayPhase.setOnClickListener {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Log.d("DayPhaseActivity", "Continuing to the next round.")
                        GameController.incrementRound()

                        // Proceed to the next night phase
                        val intent = Intent(this, NightPhaseActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    // Method to start a countdown timer for the discussion
    private fun startDiscussionTimer(isTiyanakEventActive: Boolean) {
        var discussionTimeMillis: Long = 120000 // Default 2 minutes

        if (isTiyanakEventActive) {
            discussionTimeMillis = 60000 // Reduce discussion time to 1 minute
            tvDayPhasePrompt.text = "Tiyanak has affected the discussion time. You only have 1 minute to discuss!"
        }

        // Set up the countdown timer
        object : CountDownTimer(discussionTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000) % 60
                val minutes = (millisUntilFinished / 1000) / 60
                tvTimer.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                tvTimer.text = "00:00"
                tvDayPhasePrompt.text = "Discussion time is over. Mabini will now decide who to vote."
                playMorningOnEndAudio()
                btnDayPhase.text = "Next Phase"
            }
        }.start()
    }

    // Method to play morning_on_end audio
    private fun playMorningOnEndAudio() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, R.raw.morning_on_end)
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
