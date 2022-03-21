package com.tesisunl.appod

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.Klaxon
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.socket.client.Socket
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private var scale: Float = 0f
    private var widthTextView: Int = 0
    private val listTextView = ArrayList<TextView>()
    private var detected = false
    private lateinit var observer: TextView
    private lateinit var detectorImageView: ImageView
    private lateinit var viewPager: ViewPager2
    private lateinit var menuBar: BottomNavigationView
    private val params: LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        detectorImageView = findViewById(R.id.streamingView)
        viewPager = findViewById(R.id.viewPager)
        menuBar = findViewById(R.id.bottomNavigation)
        observer = findViewById(R.id.txtDetection)
        val gridStatus = findViewById<GridLayout>(R.id.grid_activity)

        detectorImageView.setImageResource(R.drawable.no_image_foreground)

        computeTextViewWidth()

        SocketHandler.setSocket()
        val mSocket = SocketHandler.getSocket()
        mSocket.connect()
            .on(Socket.EVENT_CONNECT) {
                runOnUiThread {
                    observer.text = "NORMAL"
                    observer.setBackgroundColor(Color.parseColor("#1976D2"))
                }
            }.on(Socket.EVENT_CONNECT_ERROR) {
                runOnUiThread {
                    Toast.makeText(this, "Error in websockets connection", Toast.LENGTH_LONG).show()
                }
            }.on(Socket.EVENT_DISCONNECT) {
                runOnUiThread {
                    Toast.makeText(this, "Connection with websockets has ended", Toast.LENGTH_LONG)
                        .show()
                }
            }

        val url = "http://192.168.1.9:5000/api/get_systems"
        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                val result: List<System>? = Klaxon().parseArray(response)
                if (result != null) {
                    var cameras = 0
                    for (system in result) {
                        for (i in 0 until system.cameras) {
                            val textView = createTextViews(cameras + 1)
                            listTextView.add(textView)
                            gridStatus.addView(listTextView[cameras], cameras)
                            cameras += 1
                        }
                    }

                    val fragments: ArrayList<Fragment> = ArrayList()
                    for (i in 0 until listTextView.size) {
                        fragments.add(PageFragment())

                        val bundle = Bundle()
                        bundle.putInt("id", (i + 1))
                        fragments[i].arguments = bundle
                    }
                    val adapter = ViewPagerAdapter(fragments, this)
                    viewPager.adapter = adapter

                    initSockets(mSocket)
                }
            }, { error ->
                if (error != null)
                    println(error)
            })

        queue.add(stringRequest)
        setListeners()
    }

    fun setListeners() {
        observer.setOnClickListener {
            if (detected) {
                detected = false
                observer.text = "NORMAL"
                observer.setBackgroundColor(Color.parseColor("#1976D2"))
            }
        }
        menuBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.ic_next -> {
                    val current = viewPager.currentItem
                    if(current < (listTextView.size - 1))
                        viewPager.setCurrentItem(current + 1, true)
                }
                R.id.ic_prev -> {
                    val current = viewPager.currentItem
                    if(current > 0)
                        viewPager.setCurrentItem(current - 1, true)
                }
                else -> {  }
            }
            return@setOnItemSelectedListener true
        }
    }

    fun initSockets(mSocket: Socket) {
        for (i in 0 until listTextView.size) {
            mSocket.on("detection${i + 1}") { args ->
                if (args[0] != null) {
                    val response = args[0] as JSONObject
                    val detection: Detection? = Klaxon().parse(response.toString())
                    var strColor = "#BDBDBD"
                    if (detection != null) {
                        strColor = if (detection.movement)
                            if (detection.detection)
                                "#FF7043"
                            else
                                "#FBC02D"
                        else
                            "#1976D2"
                    }
                    runOnUiThread {
                        listTextView[i].setBackgroundColor(Color.parseColor(strColor))
                        if (detection != null) {
                            if (detection.detection) {
                                detected = true
                                observer.text = "¡INTRUSOS!"
                                observer.setBackgroundColor(Color.parseColor("#FF7043"))
                            }
                        }
                    }
                }
            }
        }
        mSocket.on("detected") { args ->
            if (args[0] != null) {
                val bytes = args[0] as ByteArray
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                runOnUiThread {
                    detectorImageView.setImageBitmap(Bitmap.createBitmap(bmp))
                }
            }
        }
    }

    private fun createTextViews(index: Int): TextView {
        val textView = TextView(this)
        textView.id = View.generateViewId()
        textView.layoutParams = params
        textView.setBackgroundColor(Color.parseColor("#BDBDBD"))
        textView.setTextColor(Color.parseColor("#212121"))
        textView.setTypeface(textView.typeface, Typeface.BOLD)
        textView.gravity = Gravity.CENTER
        textView.width = widthTextView
        textView.height = (50 * scale + 0.5f).toInt()
        textView.text = "CÁMARA$index"
        return textView
    }

    private fun computeTextViewWidth() {
        scale = resources.displayMetrics.density
        val screenWidth = resources.displayMetrics.widthPixels
        val gridInsets = ((2 * scale + 0.5f).toInt()) * 2
        val textViewMargin = ((2 * scale + 0.5f).toInt()) * 2
        widthTextView = ((screenWidth - gridInsets) / 4) - (textViewMargin * 2)
        params.setMargins(textViewMargin, textViewMargin, textViewMargin, textViewMargin)
    }
}