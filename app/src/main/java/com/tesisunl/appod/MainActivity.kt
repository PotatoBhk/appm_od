package com.tesisunl.appod

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.Klaxon
import io.socket.client.Socket
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private var scale: Float = 0f
    private var widthTextView: Int = 0
    private val params: LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    private val listTextView = ArrayList<TextView>()
    private var detected = false
    private lateinit var observer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val detectorImageView = findViewById<ImageView>(R.id.streamingView)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val gridStatus = findViewById<GridLayout>(R.id.grid_activity)
        observer = findViewById(R.id.txtDetection)

        detectorImageView.setImageResource(R.drawable.no_image_foreground)

        computeTextViewWidth()

        SocketHandler.setSocket()
        val mSocket = SocketHandler.getSocket()
        mSocket.connect().on(Socket.EVENT_CONNECT) {
            runOnUiThread {
                observer.text = "NORMAL"
                observer.setBackgroundColor(Color.parseColor("#1976D2"))
            }
        }

        val url = "http://10.0.2.2:3000/api/get_systems"
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

        observer.setOnClickListener {
            if(detected) {
                detected = false
                observer.text = "NORMAL"
                observer.setBackgroundColor(Color.parseColor("#1976D2"))
            }
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