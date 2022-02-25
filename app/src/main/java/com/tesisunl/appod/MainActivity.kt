package com.tesisunl.appod

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val tvTestSocket = findViewById<TextView>(R.id.tvTestSocket)
        val imageTest = findViewById<ImageView>(R.id.streamingView)

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