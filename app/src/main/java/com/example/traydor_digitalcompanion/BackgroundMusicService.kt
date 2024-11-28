package com.example.traydor_digitalcompanion

import android.app.Service
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.IBinder

class BackgroundMusicService : Service() {
    internal lateinit var player: MediaPlayer
    override fun onBind(arg0: Intent): IBinder? {

        return null
    }

    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer.create(applicationContext, R.raw.bg_music)
        player.isLooping = true // Set looping
        player.setVolume(0.15f, 0.15f)

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        player.start()
        return START_STICKY
    }

    override fun onStart(intent: Intent, startId: Int) {
        // TO DO
    }

    override fun onDestroy() {
        player.stop()
        player.release()
    }

    override fun onLowMemory() {

    }

    companion object {
        private val TAG: String? = null
    }
}