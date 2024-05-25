//package com.example.imagemovement;
//
//public class Sample {
//    package com.example.imagemovement
//
//
//import android.R
//import android.animation.ObjectAnimator
//import android.os.Bundle
//import android.view.MotionEvent
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.constraintlayout.widget.ConstraintLayout
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import java.io.IOException
//
//
//    class MainActivity : AppCompatActivity() {
//
//        private lateinit var imageView: ImageView
//        private lateinit var pathform: ImageView
//        private lateinit var layout: ConstraintLayout
//
//        override fun onCreate(savedInstanceState: Bundle?) {
//            super.onCreate(savedInstanceState)
//
//            val location = IntArray(2)
//            val xCoordinate = pathform.getLocationOnScreen(location)
//            val yCoordinate = pathform.y
//
//            // layout = findViewById(R.id.main_layout)
//            tvResponse = findViewById<TextView>(R.id.sampletext)
//
//                    layout.setOnTouchListener { _, event ->
//                if (event.action == MotionEvent.ACTION_DOWN) {
//                    moveImageToTouch(event.x, event.y)
//                    //moveImageToTarget()
//                }
//                true
//            }
//            makeRequest();
//        }
//        private fun moveImageToTarget() {
//            val targetLocation = IntArray(2)
//            pathform.getLocationOnScreen(targetLocation)
//            val targetX = targetLocation[0].toFloat() - (imageView.width / 2)
//            val targetY = targetLocation[1].toFloat() - (imageView.height / 2)
//
//            ObjectAnimator.ofFloat(imageView, "x", targetX).apply {
//                duration = 1000 // duration in milliseconds
//                start()
//            }
//            ObjectAnimator.ofFloat(imageView, "y", targetY).apply {
//                duration = 1000 // duration in milliseconds
//                start()
//            }
//        }
//        private fun moveImageToTouch(x: Float, y: Float) {
//            val xDelta: Float = x - imageView.width / 2
//            val yDelta: Float = y - imageView.height / 2
//            ObjectAnimator.ofFloat(imageView, "x", xDelta).apply {
//                duration = 500 // duration in milliseconds
//                start()
//            }
//            ObjectAnimator.ofFloat(imageView, "y", yDelta).apply {
//                duration = 500 // duration in milliseconds
//                start()
//            }
//        }
//        private lateinit var tvResponse: TextView
//        private fun makeRequest() {
//            val client = OkHttpClient()
//            val request = Request.Builder()
//                    .url("http://localhost:8080") // Use 10.0.2.2 for local machine
//                    .build()
//
//            client.newCall(request).enqueue(object : okhttp3.Callback {
//                override fun onFailure(call: okhttp3.Call, e: IOException) {
//                    runOnUiThread {
//                        tvResponse.text = "Failed to Connect: ${e.message}"
//                    }
//                }
//
//                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
//                    if (response.isSuccessful) {
//
//                        val myResponse = response.request.body
//                        runOnUiThread {
//                            tvResponse.text = myResponse.toString()
//                        }
//                    }
//                }
//            })
//        }
//    }public
//}
