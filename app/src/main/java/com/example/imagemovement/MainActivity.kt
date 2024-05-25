package com.example.imagemovement

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.imagemovement.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.net.ssl.X509TrustManager


class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var pathform: ImageView
    private lateinit var layout: ConstraintLayout
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)





//            val keystoreResId: Int = com.example.imagemovement.R.raw.keystore  // Replace with the correct resource ID
//        var o=KeystoreLoader.loadKeystore(this,keystoreResId);
//           var webSocketEcho = WebSocketEcho({ // Handle the connected event
//                runOnUiThread {}
//            }, this, keystoreResId)
//            webSocketEcho.start()



    }
    private lateinit var webSocketClient: WebSocketClient
    //you can get your own socket key by registering to pieSocket form here
    // https://www.piesocket.com/register?plan=free
    private val socketKey = "OoxcCdu52cCwxFKF3SqRd6ZlLJW2g9OpMokNsIlw"

    private val socketListener = object : WebSocketClient.SocketListener {
        override fun onMessage(message: String) {
            Log.e("socketCheck onMessage", message)
        }

    }
    private lateinit var tvResponse: TextView
    private lateinit var error: TextView
    private lateinit var connectiontxt: TextView
    private fun makeRequest() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://localhost:8080") // Use 10.0.2.2 for local machine
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Log.i("error-error","Failed to Connect: ${e.toString()}")
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {

                    val myResponse = response.request.body
                    runOnUiThread {
//                        tvResponse.text = myResponse.toString()
                        Log.i("msg-msg","Connection : ${myResponse.toString()}")

                    }
                }
            }
        })
    }
}