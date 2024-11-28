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
import kotlin.random.Random

class NightPhaseActivity : AppCompatActivity() {

    private lateinit var imgvwNightPrompt: ImageView
    private lateinit var tvNightPhasePrompt: TextView
    private lateinit var btnNightPhase: Button
    private lateinit var mediaPlayer: MediaPlayer
    private var randomGenerator = Random(System.currentTimeMillis())
    private lateinit var intent: Intent

    // Role actions list following the specified sequence: Traydor, Heneral, Espiya, Arrepentida
    private val roleActions = listOf(
        Pair(R.drawable.traydor, "Traydor, you are allowed to open your eyes during the Night Phase. Be careful not to get caught."),
        Pair(R.drawable.heneral, "Heneral takes action."),
        Pair(R.drawable.espiya, "Espiya collects information."),
        Pair(R.drawable.arrepentida, "Arrepentida uses ability.")
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
        mediaPlayer = MediaPlayer()
        intent = Intent(this, DayPhaseActivity::class.java)

        // Initialize prompt for the first role action or reshuffling prompt if needed
        if (GameController.shouldShowReshufflePrompt() && GameController.getInitialPlayerCount() > 5) {
            // Reshuffle prompt (not final round)
            tvNightPhasePrompt.text = "Please reshuffle your cards before the night phase begins."
            mediaPlayer = MediaPlayer.create(this, R.raw.greater_5_line)
            mediaPlayer.start()
        } else if (GameController.isGameEnd()  && GameController.getInitialPlayerCount() > 5) {
            // Final round prompt (if 5 players remain)
            tvNightPhasePrompt.text = "This is the final round. Prepare yourselves!"
            mediaPlayer = MediaPlayer.create(this, R.raw.next_round_5)
            mediaPlayer.start()
        } else {
            // Normal night phase start
            tvNightPhasePrompt.text = "Night has fallen. Each role will now take their actions."
            mediaPlayer = MediaPlayer.create(this, R.raw.night_on_start)
            mediaPlayer.start()
        }

        btnNightPhase.text = "Do Action"

        btnNightPhase.setOnClickListener {

            if(mediaPlayer.isPlaying){
                mediaPlayer.stop()
            }

            when (btnNightPhase.text) {
                "Do Action" -> {
                    Log.d("NightPhaseActivity", "Button clicked. Current role action index: $currentRoleActionIndex")

                    if (currentRoleActionIndex == roleActions.size) {
                        checkForFolkloreEvent()
                    } else if (currentRoleActionIndex < roleActions.size) {
                        Log.d("NightPhaseActivity", "Attempting to display role action: ${roleActions[currentRoleActionIndex].second}")

                        tvNightPhasePrompt.text = roleActions[currentRoleActionIndex].second
                        imgvwNightPrompt.setImageResource(roleActions[currentRoleActionIndex].first)
                        Log.d("NightPhaseActivity", "Displayed role action prompt: ${tvNightPhasePrompt.text}")

                        // Play role-specific audio if applicable (excluding Traydor)
                        when (currentRoleActionIndex) {
                            1 -> {
                                mediaPlayer = MediaPlayer.create(this, R.raw.heneral_line)
                                mediaPlayer.start()
                            }
                            2 -> {
                                mediaPlayer = MediaPlayer.create(this, R.raw.espiya_line)
                                mediaPlayer.start()
                            }
                            3 -> {
                                mediaPlayer = MediaPlayer.create(this, R.raw.arrepentida_line)
                                mediaPlayer.start()
                            }
                        }

                        currentRoleActionIndex++
                        Log.d("NightPhaseActivity", "Incremented role action index to: $currentRoleActionIndex")
                    }
                }

                "Next Phase" -> {
                    mediaPlayer.release()
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
        val selectedEvent = folkloreEvents[randomGenerator.nextInt(0, folkloreEvents.size)]

        // Play folklore event start audio
        imgvwNightPrompt.setImageResource(R.drawable.eye)
        tvNightPhasePrompt.text = "Open your eyes and stay vigilant!"
        mediaPlayer = MediaPlayer.create(this, R.raw.folklore_on_start)
        mediaPlayer.start()
        btnNightPhase.visibility = View.INVISIBLE

        mediaPlayer.setOnCompletionListener {
            btnNightPhase.visibility = View.VISIBLE
            when (selectedEvent) {
                "Tikbalang" -> {
                    tvNightPhasePrompt.text = "Tikbalang appears! ${tikbalangSilencePLayer()}."
                    imgvwNightPrompt.setImageResource(R.drawable.tikbalang)
                    mediaPlayer = MediaPlayer.create(this, R.raw.tikbalang_line)
                }
                "Duwende" -> {
                    tvNightPhasePrompt.text = "Duwende is mischievous! ${duwendeSwitchRoles()}."
                    imgvwNightPrompt.setImageResource(R.drawable.duwende)
                    mediaPlayer = MediaPlayer.create(this, R.raw.duwende_line)
                }
                "Diwata" -> {
                    tvNightPhasePrompt.text = "Diwata appears! All players can look at their cards before the discussion."
                    imgvwNightPrompt.setImageResource(R.drawable.diwata)
                    mediaPlayer = MediaPlayer.create(this, R.raw.diwata_line)
                }
                "Kura Paroko" -> {
                    tvNightPhasePrompt.text = "Kura Paroko appears! It's a peaceful night with no special effects."
                    imgvwNightPrompt.setImageResource(R.drawable.kura_paroka)
                    mediaPlayer = MediaPlayer.create(this, R.raw.cura_parroco_line)
                }
                "Kapre" -> {
                    tvNightPhasePrompt.text = "Kapre appears! The roles are switched. Traydor, choose who you think is Mabini. Mabini, lay low!"
                    imgvwNightPrompt.setImageResource(R.drawable.kapre)
                    mediaPlayer = MediaPlayer.create(this, R.raw.kapre_line)
                    intent.putExtra("KapreEventActive", true)
                }
                "Tiyanak" -> {
                    tvNightPhasePrompt.text = "Tiyanak appears! Press the button before time runs out, or the discussion time will be shortened."
                    imgvwNightPrompt.setImageResource(R.drawable.tiyanak)
                    btnNightPhase.text = "Press Quickly!"
                    mediaPlayer = MediaPlayer.create(this, R.raw.tiyanak_line)

                    Handler().postDelayed({
                        if (btnNightPhase.text == "Press Quickly!") {
                            tvNightPhasePrompt.text = "Tiyanak was not appeased in time! Discussion time is now shorter."
                            btnNightPhase.text = "Next Phase"
                            intent.putExtra("TiyanakEventActive", true)
                        }
                    }, 10000) // 10 seconds to press the button because of audio
                }
                "Manananggal" -> {
                    tvNightPhasePrompt.text = "Manananggal appears! Everyone can vote this round, not just the Mabini. The majority wins."
                    imgvwNightPrompt.setImageResource(R.drawable.manananggal)
                    mediaPlayer = MediaPlayer.create(this, R.raw.mananangal_line)
                    intent.putExtra("ManananggalEventActive", true)
                }
            }
            mediaPlayer.start()
        }

        if (selectedEvent != "Tiyanak") {
            btnNightPhase.text = "Next Phase"
        }
    }

    fun duwendeSwitchRoles() : String {
        val playerNum = GameController.getCurrPlayerCount()
        val firstPlayerNum = randomGenerator.nextInt(0, playerNum)
        var secondPlayerNum = randomGenerator.nextInt(0, playerNum)

        if(secondPlayerNum == firstPlayerNum){
            secondPlayerNum++
            if(secondPlayerNum > playerNum - 1 || secondPlayerNum < 0) secondPlayerNum -= 2
        }

        lateinit var firstPlayer: String
        lateinit var secondPlayer: String

        when(firstPlayerNum){
            0 -> firstPlayer = "player reading this"
            1 -> firstPlayer = "first player to my left"
            2 -> firstPlayer = "second player to my left"
            3 -> firstPlayer = "third player to my left"
            4 -> firstPlayer = "fourth player to my left"
            5 -> firstPlayer = "fifth player to my left"
            6 -> firstPlayer = "sixth player to my left"
            7 -> firstPlayer = "seventh player to my left"
            8 -> firstPlayer = "eighth player to my left"
            9 -> firstPlayer = "ninth player to my left"
        }

        when(secondPlayerNum){
            0 -> secondPlayer = "player reading this"
            1 -> secondPlayer = "first player to my left"
            2 -> secondPlayer = "second player to my left"
            3 -> secondPlayer = "third player to my left"
            4 -> secondPlayer = "fourth player to my left"
            5 -> secondPlayer = "fifth player to my left"
            6 -> secondPlayer = "sixth player to my left"
            7 -> secondPlayer = "seventh player to my left"
            8 -> secondPlayer = "eighth player to my left"
            9 -> secondPlayer = "ninth player to my left"
        }

        return "The ${firstPlayer} and the ${secondPlayer} must exchange their roles"
    }

    fun tikbalangSilencePLayer() : String {
        val playerCount = GameController.getCurrPlayerCount()
        val playerNum = randomGenerator.nextInt(0, playerCount)

        lateinit var player: String

        when(playerNum){
            0 -> player = "player reading this"
            1 -> player = "first player to my left"
            2 -> player = "second player to my left"
            3 -> player = "third player to my left"
            4 -> player = "fourth player to my left"
            5 -> player = "fifth player to my left"
            6 -> player = "sixth player to my left"
            7 -> player = "seventh player to my left"
            8 -> player = "eighth player to my left"
            9 -> player = "ninth player to my left"
        }

        return "The ${player} is silenced for the next discussion"
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
