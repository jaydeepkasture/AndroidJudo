package com.example.imagemovement

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocketListener
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.InputStream
import java.security.KeyStore
import java.security.Security
import java.util.concurrent.TimeUnit
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

open class WebSocketClient(private val context: Context, private val keystoreResId: Int) {
    private lateinit var webSocket: okhttp3.WebSocket
    private var socketListener: SocketListener? = null
    private var socketUrl = "wss://172.26.64.1:8080"
    private var shouldReconnect = true
    private var client: OkHttpClient? = null

    companion object {
        private lateinit var instance: WebSocketClient
        @JvmStatic
        @Synchronized
        // This function gives singleton instance of WebSocket.
        fun getInstance(context: Context, keystoreResId: Int): WebSocketClient {
            synchronized(WebSocketClient::class) {
                if (!::instance.isInitialized) {
                    instance = WebSocketClient(context, keystoreResId)
                }
            }
            return instance
        }
    }

    fun setListener(listener: SocketListener) {
        this.socketListener = listener
    }

    fun setSocketUrl(socketUrl: String) {
        this.socketUrl = socketUrl
    }

    private fun initWebSocket() {
        Log.e("socketCheck", "initWebSocket() socketurl = $socketUrl")
        client = getSecureHttpClient()
        val request = Request.Builder().url(socketUrl).build()
        webSocket = client!!.newWebSocket(request, webSocketListener)
        // This must be done to avoid memory leak
        client!!.dispatcher.executorService.shutdown()
    }

    private fun getSecureHttpClient(): OkHttpClient {
        try {
            // Add BouncyCastle as a security provider
            if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
                Security.addProvider(BouncyCastleProvider())
            }

            // Load the .p12 file from res/raw using the resource ID
            val keystoreStream: InputStream = context.resources.openRawResource(keystoreResId)
            val keyStore = KeyStore.getInstance("PKCS12", BouncyCastleProvider.PROVIDER_NAME)
            keyStore.load(keystoreStream, "123".toCharArray())

            // Initialize KeyManagerFactory with the .p12 file
            val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            keyManagerFactory.init(keyStore, "123".toCharArray())

            // Initialize TrustManagerFactory with the .p12 file
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)

            // Create TrustManager
            val trustManagers = trustManagerFactory.trustManagers
            val trustManager = trustManagers[0] as X509TrustManager

            // Initialize SSLContext with KeyManagerFactory and TrustManagerFactory
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(keyManagerFactory.keyManagers, trustManagers, null)

            return OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustManager)
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build()
        } catch (e: Exception) {
            throw RuntimeException("Error initializing SSL context", e)
        }
    }

    fun connect() {
        Log.e("socketCheck", "connect()")
        shouldReconnect = true
        initWebSocket()
    }

    fun reconnect() {
        Log.e("socketCheck", "reconnect()")
        initWebSocket()
    }

    // Send a message
    fun sendMessage(message: String) {
        Log.e("socketCheck", "sendMessage($message)")
        if (::webSocket.isInitialized) webSocket.send(message)
    }

    // Disconnect the WebSocket
    fun disconnect() {
        if (::webSocket.isInitialized) webSocket.close(1000, "Do not need connection anymore.")
        shouldReconnect = false
    }

    interface SocketListener {
        fun onMessage(message: String)
    }

    private val webSocketListener = object : WebSocketListener() {
        // Called when connection succeeded
        override fun onOpen(webSocket: okhttp3.WebSocket, response: Response) {
            Log.e("socketCheck", "onOpen()")
        }

        // Called when a text message is received
        override fun onMessage(webSocket: okhttp3.WebSocket, text: String) {
            socketListener?.onMessage(text)
        }

        // Called when the WebSocket is closing
        override fun onClosing(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
            Log.e("socketCheck", "onClosing()")
        }

        // Called when the WebSocket is closed
        override fun onClosed(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
            Log.e("socketCheck", "onClosed()")
            if (shouldReconnect) reconnect()
        }

        // Called when a WebSocket error occurs
        override fun onFailure(webSocket: okhttp3.WebSocket, t: Throwable, response: Response?) {
            Log.e("socketCheck", "onFailure()")
            if (shouldReconnect) reconnect()
        }
    }
}
