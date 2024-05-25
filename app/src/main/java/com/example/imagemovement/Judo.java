package com.example.imagemovement;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.imagemovement.sslconnect.KeyStoreUtil;
import com.example.imagemovement.sslconnect.SocketWeb;
import com.example.imagemovement.sslconnect.WebSocketHelper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.SecureRandom;

public class Judo extends AppCompatActivity {

    private static final String TAG = "Judo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            SocketWeb.GetSocket();
//           WebSocketHelper webSocketHelper = new WebSocketHelper(this);
//            webSocketHelper.connectWebSocket();
//            String keystorePassword = "123";
//            int keyResId = R.raw.key; // Your private key resource ID
//            int certResId = R.raw.crt; // Your certificate resource ID
//
//            // Create KeyStore
//            KeyStore keyStore = KeyStoreUtil.createKeyStore(this, keyResId, certResId, keystorePassword);
//
//            // Create SSLContext
//            SSLContext sslContext = createSSLContext(keyStore, keystorePassword);
//
//            // Create OkHttpClient with SSLSocketFactory
//            OkHttpClient client = new OkHttpClient.Builder()
//                    .sslSocketFactory(sslContext.getSocketFactory(), getTrustManager(keyStore))
//                    .build();
//
//            // Create WebSocket request
//            Request request = new Request.Builder()
//                    .url("wss://www.wslphone.com:8080")
//                    .build();
//
//            // Create WebSocket listener
//            WebSocketListener listener = new SecureWebSocketListener();
//
//            // Connect to WebSocket
//            client.newWebSocket(request, listener);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error: " + e.toString());
        }
    }

    private SSLContext createSSLContext(KeyStore keyStore, String keystorePassword) throws Exception {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
        return sslContext;
    }

    private X509TrustManager getTrustManager(KeyStore keyStore) throws Exception {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);
        TrustManager[] trustManagers = tmf.getTrustManagers();
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        throw new IllegalStateException("No X509TrustManager found");
    }

    private static class SecureWebSocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Log.d(TAG, "WebSocket Opened");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.d(TAG, "WebSocket Message: " + text);
        }

        @Override
        public void onMessage(WebSocket webSocket, okio.ByteString bytes) {
            Log.d(TAG, "WebSocket Binary Message: " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(1000, null);
            Log.d(TAG, "WebSocket Closing: " + code + " / " + reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Log.d(TAG, "WebSocket Closed: " + code + " / " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            Log.e(TAG, "WebSocket Failure: " + t.getMessage(), t);
        }
    }
}
