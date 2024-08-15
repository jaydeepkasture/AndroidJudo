package com.example.imagemovement

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.GridLayout
import com.example.imagemovement.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var pawn: ImageView
    private lateinit var binding: ActivityMainBinding
    private lateinit var gridLayout: GridLayout
    private lateinit var spots: Array<ImageView>
    private lateinit var spawnedTextView: GridLayout
    private lateinit var txt1: TextView
    private lateinit var txt2: TextView
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
           // setHeightOfgridlayout()
            txt1=findViewById(R.id.txt1)
            txt2=findViewById(R.id.txt2)
            // Ensure GridLayout has children and get the position of the first child
            gridLayout.post {

                var child=gridLayout.getChildAt(1)
                var x = child.width
                var y = child.height;
//                pawn=findViewById(R.id.pawn)
                pawn = ImageView(this).apply {
                    setImageResource(R.drawable.google_authenticator_icon) // Set your image here
                    layoutParams = ConstraintLayout.LayoutParams(x, y)
                }
                val rootLayout = findViewById<ConstraintLayout>(R.id.main_layout)
                rootLayout.addView(pawn)

                constraintLayout = findViewById(R.id.main_layout)
                Log.d(tag,"height $y widht $x")
                var i= intArrayOf(91,92,93,94,95)
                moveImageToSpots(i) // Pass the array of indices

            }
        }catch (e:Exception){
            Log.d("board",e.toString())
        }

    }

    private fun setHeightOfgridlayout() {
        gridLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the listener to prevent it from being called multiple times
                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                val height = displayMetrics.heightPixels
                val width = displayMetrics.widthPixels
                gridLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Get the width of the GridLayout
                val gridWidth = gridLayout.width

                // Set the height of the GridLayout equal to its width
                val layoutParams = gridLayout.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.height = width
                layoutParams.width = width
                gridLayout.layoutParams = layoutParams
            }
        })
    }

    private fun moveImageToSpots(indices: IntArray) {
        val handler = Handler(Looper.getMainLooper())
        var currentIndex = 0

        val runnable = object : Runnable {
            override fun run() {
                if (currentIndex < indices.size) {
                    val index = currentIndex
                    val targetSpot = gridLayout.getChildAt(indices[index]) as ImageView
                    val location = IntArray(2)
                    val pawnLocation = IntArray(2)
                    targetSpot.getLocationOnScreen(location)
                    pawn.getLocationOnScreen(pawnLocation)
                    val targetX = location[0]
                    val targetY = location[1]

                    pawn.setX(targetX.toFloat());
                    pawn.setY(targetY.toFloat() );
//                  pawn.animate().x(dpX).y(dpY).setDuration(300)
//                  ObjectAnimator.ofFloat(pawn, "translationX", pawn.x, targetSpot.x).apply {
//                      duration = 500
//                      start()
//                  }
//                  ObjectAnimator.ofFloat(pawn, "translationY", pawn.y, targetSpot.y).apply {
//                      duration = 500
//                      start()
//                  }
                    currentIndex++
                    handler.postDelayed(this, 500) // Delay for animation
                    txt1.text="spot x:"+location[0]+" y:"+location[1]+" width:"+targetSpot.width+" height:"+targetSpot.height;
                    txt2.text="pawn x:"+pawn.x+" y:"+pawn.y+" width:"+pawn.width+" height:"+pawn.height;
                    return
                }
            }
        }

        handler.post(runnable)
    }
    fun Context.getScreenWidthInDp(): Float {
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val screenWidthPx = displayMetrics.widthPixels
        return screenWidthPx / displayMetrics.density
    }
    fun convertDpToPixel(dp: Float, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

}
