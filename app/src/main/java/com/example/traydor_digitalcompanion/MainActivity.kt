package com.example.traydor_digitalcompanion

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.media.MediaPlayer

class MainActivity : AppCompatActivity() {
    private lateinit var imgTopSun : ImageView
    private lateinit var imgBottomSun : ImageView
    private lateinit var btnStart : Button
    private lateinit var btnHowToPlay : Button
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        imgTopSun = findViewById(R.id.imgvwTopSun)
        imgBottomSun = findViewById(R.id.imgvwBottomSun)
        btnStart = findViewById(R.id.btnStartGame)
        btnHowToPlay = findViewById(R.id.btnHowToPlay)

        val sunAnimation = AnimationUtils.loadAnimation(this, R.anim.sun_rotation_clockwise)
        val sunCCAnimation = AnimationUtils.loadAnimation(this, R.anim.sun_rotation_counterclockwise)
        imgTopSun.startAnimation(sunAnimation)
        imgBottomSun.startAnimation(sunCCAnimation)

        btnStart.setOnClickListener(){
            val intent = Intent(applicationContext, PreparationPhase::class.java)
            startActivity(intent)
            finish()
        }

        btnHowToPlay.setOnClickListener() {
            val intent = Intent(applicationContext, HowToPlayActivity::class.java)
            startActivity(intent)
        }
    }
}