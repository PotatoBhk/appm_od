package com.tesisunl.appod

import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class AlertActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
//        val txtAlarm : TextView = findViewById(R.id.camera_alarm)
        val btnClose : Button = findViewById(R.id.btn_close)
//        val nCamera : String? = intent.getStringExtra("nCamera")
//        txtAlarm.text = "CÁMARA $nCamera"
        val mediaPlayer = MediaPlayer.create(this, R.raw.alarm)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
        btnClose.setOnClickListener {
            mediaPlayer.stop()
            finish()
        }
    }
}