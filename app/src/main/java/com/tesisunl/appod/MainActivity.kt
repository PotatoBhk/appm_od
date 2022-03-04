package com.tesisunl.appod

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.net.http.HttpResponseCache.install
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


class MainActivity : AppCompatActivity() {
    var scale: Float = 0f
    var widthTextView: Int = 0
    val params: LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    val listTextView = ArrayList<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val tvTestSocket = findViewById<TextView>(R.id.tvTestSocket)
        val detectorImageView = findViewById<ImageView>(R.id.streamingView)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val gridStatus = findViewById<GridLayout>(R.id.grid_activity)

        detectorImageView.setImageResource(R.mipmap.test_resource_b_foreground)

        computeTextViewWidth()

//        SocketHandler.setSocket()
//        val mSocket = SocketHandler.getSocket()
//        mSocket.connect()

        val client = HttpClient {
            install(WebSockets)
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

//                    val fragments: ArrayList<Fragment> = ArrayList()
//                    for(i in 0 until listTextView.size) {
//                        fragments.add(PageFragment())
//
//                        val bundle = Bundle()
//                        bundle.putInt("id", (i + 1))
//                        fragments[i].arguments = bundle
//                    }
//                    val adapter = ViewPagerAdapter(fragments, this)
//                    viewPager.adapter = adapter
                }
            }, { error ->
                if (error != null)
                    println(error)
            })

        queue.add(stringRequest)

//        SocketHandler.setSocket()
//
//        val mSocket = SocketHandler.getSocket()
//
//        mSocket.connect()

//        mSocket.on("newnumber") { args ->
//            if(args[0] != null) {
//                println("In....")
//                val numbers = args[0] as String
//                runOnUiThread {
//                    tvTestSocket.text = numbers
//                }
//            }
//        }

        mSocket.on("video1") { args ->
            println("hi")
            if(args[0] != null) {
                val bytes = args[0] as ByteArray
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                runOnUiThread {
                    detectorImageView.setImageBitmap(Bitmap.createBitmap(bmp))
                }
            }
        }
    }

    fun createTextViews(index: Int): TextView {
        val textView = TextView(this)
        textView.id = View.generateViewId()
        textView.layoutParams = params
        textView.setBackgroundColor(Color.parseColor("#75E900"))
        textView.setTextColor(Color.parseColor("#212121"))
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD)
        textView.gravity = Gravity.CENTER
        textView.width = widthTextView
        textView.height = (50 * scale + 0.5f).toInt()
        textView.text = "CÁMARA$index"
        return textView
    }

    fun computeTextViewWidth() {
        scale = resources.displayMetrics.density
        val screenWidth = resources.displayMetrics.widthPixels
        val gridInsets = ((2 * scale + 0.5f).toInt()) * 2
        val textViewMargin = ((2 * scale + 0.5f).toInt()) * 2
        widthTextView = ((screenWidth - gridInsets) / 4) - (textViewMargin * 2)
        params.setMargins(textViewMargin, textViewMargin, textViewMargin, textViewMargin)
    }
}