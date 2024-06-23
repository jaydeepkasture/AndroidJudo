package com.example.imagemovement

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.gridlayout.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.imagemovement.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var pathform: ImageView
    private lateinit var binding: ActivityMainBinding
    private lateinit var gridLayout: GridLayout
    private lateinit var spots: Array<TextView>
    private lateinit var spawnedTextView: TextView
    private lateinit var constraintLayout: ConstraintLayout
    val bluepath= intArrayOf(133,132,131,130,129,143,158,173,188,203,218,217,216,201,186,171,156,141,125,124,123,122,121,120,105,90,91,92,93,94,95,81,66,51,36,21,6,7,8,23,38,53,68,83,99,100,101,103,104,119,118,117,116,115,114,113)
     val yellowPath= intArrayOf( 23,38,53,68,83,99,100,101,102,103,104,119,134,133,132,131,130,129,143,158,173,188,203,218,217,216,201,186,171,156,141,125,124,123,122,121,120,105,90,91,92,93,94,95,81,66,51,36,21,6,7,22,37,52,67,82,97);
 val redpath= intArrayOf(201,186,171,156,141,125,124,123,122,121,120,105,90,91,92,93,94,95,81,66,51,36,21,6,7,8,23,38,53,68,83,99,100,101,102,103,104,119,134,133,132,131,130,129,143,158,173,188,203,218,217,202,187,172,157,142,127)
  val greenpath= intArrayOf(91,92,93,94,95,81,66,51,36,21,6,7,8,23,38,53,68,83,99,100,101,102,103,104,119,134,133,132,131,130,129,143,158,173,188,203,218,217,216,201,186,171,156,141,125,124,123,122,121,120,105,106,107,108,109,110,111)
    val safeSpot= intArrayOf(188,201,122,91,36,23,102,133)
    private  var tag: String="Board"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {

            gridLayout = findViewById(R.id.board)


            // Ensure GridLayout has children and get the position of the first child
            gridLayout.post {

                for (index in 0 until gridLayout.childCount) {
                    var txtview=gridLayout.getChildAt(index) as TextView
                    txtview.text=index.toString()
                    txtview.setTextColor(Color.parseColor("#FFFFFF"))
                    txtview.textAlignment=TextView.TEXT_ALIGNMENT_CENTER;
                    txtview.setTextSize(10f)
                    txtview.gravity = Gravity.CENTER
                    txtview.setTextIsSelectable(true)
                }
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
               moveImageToSpots(greenpath) // Pass the array of indices

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
