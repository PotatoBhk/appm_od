package com.tesisunl.appod

import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val tvTestSocket = findViewById<TextView>(R.id.tvTestSocket)
        val detectorImageView = findViewById<ImageView>(R.id.streamingView)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val gridStatus = findViewById<GridLayout>(R.id.grid_activity)
        gridStatus.columnCount = 3

        detectorImageView.setImageResource(R.mipmap.test_resource_b_foreground)

        val bundleA = Bundle()
        val bundleB = Bundle()
        bundleA.putInt("resource", R.mipmap.test_resource_a)
        bundleB.putInt("resource", R.mipmap.test_resource_b)

        val fragments: ArrayList<Fragment> = arrayListOf(
            PageFragment(),
            PageFragment(),
            PageFragment()
        )

        val textView = TextView(this)
        textView.setPadding(5,5,5,5)
        textView.setBackgroundColor(getColor(androidx.appcompat.R.color.material_blue_grey_800))
        textView.setTextColor(getColor(R.color.black))

//        val arrayTextView : ArrayList<TextView> = ArrayList()

//        for (i in 1..5) {
//            textView.text = "Botón #$i"
//            gridStatus.addView(textView)
//        }

        fragments[0].arguments = bundleA
        fragments[1].arguments = bundleB
        val adapter = ViewPagerAdapter(fragments, this)
        viewPager.adapter = adapter

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

//        mSocket.on("video") { args ->
//            if(args[0] != null) {
//                val bytes = args[0] as ByteArray
//                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//                runOnUiThread {
//                    imageTest.setImageBitmap(Bitmap.createBitmap(bmp))
//                }
//            }
//        }
    }
}