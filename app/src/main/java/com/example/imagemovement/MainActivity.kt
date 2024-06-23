package com.example.imagemovement

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.gridlayout.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.imagemovement.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var pathform: ImageView
    private lateinit var binding: ActivityMainBinding
    private lateinit var gridLayout: GridLayout
    private lateinit var spots: Array<ImageView>
    private lateinit var spawnedImageView: ImageView
    private lateinit var constraintLayout: ConstraintLayout
    private  var tag: String="Board"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {

            gridLayout = findViewById(R.id.board)


            // Ensure GridLayout has children and get the position of the first child
            gridLayout.post {

                var child=gridLayout.getChildAt(0)
                var y = child.width
                var x = child.height;
                pathform = ImageView(this).apply {
                    setImageResource(R.drawable.adobe) // Set your image here
                    layoutParams = ConstraintLayout.LayoutParams(x/2, y/2)
                }
                val rootLayout = findViewById<ConstraintLayout>(R.id.main_layout)
                rootLayout.addView(pathform)

                constraintLayout = findViewById(R.id.main_layout)
                Log.d(tag,"height $y widht $x")
                val myArray1 = intArrayOf(1,  46,90,3,20,34,120,20,101,40,94,120,144)
                    moveImageToSpots(myArray1) // Pass the array of indices

            }
        }catch (e:Exception){
            Log.d("board",e.toString())
        }

    }

    private fun moveImageToSpots(indices: IntArray) {
        val handler = Handler(Looper.getMainLooper())
        var currentIndex = 0

        val runnable = object : Runnable {
            override fun run() {
                if (currentIndex < indices.size) {
                    val index = currentIndex
                        val targetSpot = gridLayout.getChildAt(indices[index])
                        val location = IntArray(2)
                        targetSpot.getLocationOnScreen(location)
                        val targetX = location[0] + targetSpot.width / 2 - pathform.width / 2
                        val targetY = location[1] - targetSpot.height / 2 - pathform.height / 2

                        ObjectAnimator.ofFloat(pathform, "x", pathform.x, targetX.toFloat()).apply {
                            duration = 500
                            start()
                        }
                        ObjectAnimator.ofFloat(pathform, "y", pathform.y, targetY.toFloat()).apply {
                            duration = 500
                            start()
                        }
                    currentIndex++
                    handler.postDelayed(this, 500) // Delay for animation
                }
            }
        }

        handler.post(runnable)
    }
}
