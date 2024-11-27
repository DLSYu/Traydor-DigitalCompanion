package com.example.traydor_digitalcompanion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PlayerCountActivity : AppCompatActivity() {
    private lateinit var spnrPlayerCount : Spinner
    private lateinit var btnNextPhase : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.player_count)

        spnrPlayerCount = findViewById(R.id.spnrPlayerCount)
        btnNextPhase = findViewById(R.id.btnNextPhase)

        var playerCounts = arrayOf(5, 6, 7, 8, 9, 10)
        var arrayAdapter = ArrayAdapter(this, R.layout.spinner_item_style, playerCounts)
        spnrPlayerCount.adapter = arrayAdapter

        btnNextPhase.setOnClickListener{
            val numOfPlayers = spnrPlayerCount.selectedItem.toString().toInt()
            // Set the player count in GameController
            GameController.setInitialPlayerCount(numOfPlayers)
            Log.d("PreparationPhase", "Number of players set: $numOfPlayers")

            val intent = Intent(this, PreparationPhase::class.java)
            startActivity(intent)
            finish()
        }
    }
}