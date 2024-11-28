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
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var timer: CountDownTimer

    private var isTiyanakEventActive = false
    private var isKapreEventActive = false
    private var isManananggalEventActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_day_phase)

        // Initialize views
        imgvwDayPrompt = findViewById(R.id.imgvwDayPrompt)
        tvDayPhasePrompt = findViewById(R.id.tvDayPhasePrompt)
        tvTimer = findViewById(R.id.tvTimer)
        btnDayPhase = findViewById(R.id.btnDayPhase)
        mediaPlayer = MediaPlayer()

        tvTimer.visibility = View.INVISIBLE
        tvDayPhasePrompt.visibility = View.INVISIBLE
        btnDayPhase.visibility = View.INVISIBLE

        // Determine if the Tiyanak folklore event was active from the Night Phase
        isTiyanakEventActive = intent.getBooleanExtra("TiyanakEventActive", false)
        isKapreEventActive = intent.getBooleanExtra("KapreEventActive", false)
        isManananggalEventActive = intent.getBooleanExtra("ManananggalEventActive", false)


        // Set up the countdown timer
        var discussionTimeMillis: Long = 120000 // Default 2 minutes

        if (isTiyanakEventActive) {
            discussionTimeMillis = 60000 // Reduce discussion time to 1 minute
        }
        timer = object : CountDownTimer(discussionTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000) % 60
                val minutes = (millisUntilFinished / 1000) / 60
                tvTimer.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                if(mediaPlayer.isPlaying) mediaPlayer.stop()
                tvTimer.text = "00:00"

                if(isKapreEventActive) {
                    tvDayPhasePrompt.text = "Discussion time is over. With the blessing of the Tikbalang, the Traydor will now decide who to vote."
                    imgvwDayPrompt.setImageResource(R.drawable.traydor)
                } else if(isManananggalEventActive){
                    tvDayPhasePrompt.text = "Discussion time is over. Everyone must now decide who to vote."
                    imgvwDayPrompt.setImageResource(R.drawable.sun_with_rays)
                } else {
                    tvDayPhasePrompt.text = "Discussion time is over. Mabini will now decide who to vote."
                    imgvwDayPrompt.setImageResource(R.drawable.mabini)
                    playMorningOnEndAudio()
                }

                btnDayPhase.text = "Next Phase"
            }
        }

        // Play day phase start audio
        mediaPlayer = MediaPlayer.create(this, R.raw.morning_on_start)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            // Initialize prompt for the Day Phase
            tvDayPhasePrompt.text = "Day has started. Players will now discuss and act."
            imgvwDayPrompt.setImageResource(R.drawable.sun_with_rays)
            tvTimer.visibility = View.VISIBLE
            tvDayPhasePrompt.visibility = View.VISIBLE
            btnDayPhase.visibility = View.VISIBLE

            // Hide Next Phase button initially
            btnDayPhase.text = "Skip Timer"

            // Initialize the timer based on the Tiyanak event
            startDiscussionTimer()
        }

        btnDayPhase.setOnClickListener{

            if(mediaPlayer.isPlaying){
                mediaPlayer.stop()
            }

            when(btnDayPhase.text){
                "Skip Timer" -> {
                    timer.cancel()
                    tvTimer.text = "00:00"

                    if(isKapreEventActive) {
                        tvDayPhasePrompt.text = "Discussion time is over. With the blessing of the Tikbalang, the Traydor will now decide who to vote."
                        imgvwDayPrompt.setImageResource(R.drawable.traydor)
                    } else if(isManananggalEventActive){
                        tvDayPhasePrompt.text = "Discussion time is over. Everyone must now decide who to vote."
                        imgvwDayPrompt.setImageResource(R.drawable.sun_with_rays)
                    } else {
                        tvDayPhasePrompt.text = "Discussion time is over. Mabini will now decide who to vote."
                        imgvwDayPrompt.setImageResource(R.drawable.mabini)
                        playMorningOnEndAudio()
                    }

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
    private fun startDiscussionTimer() {
        if (isTiyanakEventActive) {
            tvDayPhasePrompt.text =
                "Tiyanak has affected the discussion time. You only have 1 minute to discuss!"
        }
        timer.start()
    }

    // Method to play morning_on_end audio
    private fun playMorningOnEndAudio() {
        mediaPlayer.release()
        mediaPlayer = MediaPlayer.create(this, R.raw.morning_on_end)
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
