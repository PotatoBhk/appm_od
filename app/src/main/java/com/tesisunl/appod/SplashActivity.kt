package com.tesisunl.appod

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        resetPref()
        val pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE)
        if (pref.getBoolean("activity_executed", false)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    fun resetPref() {
        val pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("activity_executed", false)
        editor.apply()
    }
}
