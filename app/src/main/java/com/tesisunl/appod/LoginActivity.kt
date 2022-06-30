package com.tesisunl.appod

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.Klaxon
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class LoginActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val loginBtn : Button = findViewById(R.id.loginBtn)
        val server : TextInputEditText = findViewById(R.id.serverLoginti)
        val mail : TextInputEditText = findViewById(R.id.mailLoginti)
        val user : TextInputEditText = findViewById(R.id.userLoginti)
        val password : TextInputEditText = findViewById(R.id.passwordLoginti)
//        val queue : RequestQueue = Volley.newRequestQueue(this)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        loginBtn.setOnClickListener {
            val serverStr : String = server.text.toString()
            val userStr : String = user.text.toString()
            val passwordStr : String = password.text.toString()
            val mailStr : String = mail.text.toString()
            val jsonObject = JSONObject()
            jsonObject.put("id", 0)
            jsonObject.put("username", userStr)
            jsonObject.put("email", mailStr)
            jsonObject.put("password", passwordStr)
            println(jsonObject.toString())
            val url = "http://$serverStr/login"
            println(url)
            val requestBody = jsonObject.toString().toRequestBody(mediaType)
            val request = okhttp3.Request.Builder()
                .post(requestBody)
                .url(url)
                .build()
            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                    println("Failure")
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    println("Response")
                    val responseStr = response.body?.string()
                    println(responseStr)
                    val result : ResponseLogin = Klaxon().parse(responseStr!!)!!
                    if (!result.status.booleanValue()) {
                        Toast.makeText(this@LoginActivity, "Error en la conexión con el servidor",
                            Toast.LENGTH_LONG).show()
                        return
                    }
                    if (result.isValid.booleanValue()) {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("server", serverStr)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@LoginActivity, "Credenciales incorrectas",
                            Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }
    fun setLoginInitiated() {
        val pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("activity_executed", true)
        editor.apply()
    }
}