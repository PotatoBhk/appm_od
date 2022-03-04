package com.tesisunl.appod

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PageFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null) {
            val id = requireArguments().getInt("id")
            val streamContainer = view.findViewById<ImageView>(R.id.streamContainer)
            streamContainer.setImageResource(R.mipmap.test_resource_a_foreground)

            val mSocket = SocketHandler.getSocket()
            println("Fragment with id: $id")
            mSocket.on("video$id") { args ->
                if(args[0] != null) {
                    val bytes = args[0] as ByteArray
                    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    activity?.runOnUiThread {
                        println("In...")
                        streamContainer.setImageBitmap(Bitmap.createBitmap(bmp))
                    }
                }
            }
        }
    }
}