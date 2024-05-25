package com.example.imagemovement;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public final class WebSocketEcho extends WebSocketListener {
    private static final String TAG = "WebSocketEcho";

    private WebSocketListenerCallback callback;
    private Context context;
    private int keystoreResId = R.raw.keystoreo;
    private static final String KEYSTORE_PASSWORD = "123456";
    private WebSocket webSocket;

    public WebSocketEcho(WebSocketListenerCallback callback, Context context, int keystoreResId) {
        this.callback = callback;
        this.context = context;
        this.keystoreResId = keystoreResId;
    }

    public void start() {
        try {
            validatekeystore();
            // Load the .p12 file from res/raw using the resource ID
            InputStream keystoreStream = context.getResources().openRawResource(keystoreResId);
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(keystoreStream, KEYSTORE_PASSWORD.toCharArray());

            // Initialize KeyManagerFactory with the .p12 file
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, KEYSTORE_PASSWORD.toCharArray());

            // Initialize TrustManagerFactory with the .p12 file
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            // Create TrustManager
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            final X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

            // Initialize SSLContext with KeyManagerFactory and TrustManagerFactory
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagers, null);

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                    .readTimeout(0, TimeUnit.MILLISECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url("wss://172.26.64.1:8080")
                    .build();
            webSocket = client.newWebSocket(request, this);

            client.dispatcher().executorService().shutdown();
        } catch (Exception e) {
            Log.e(TAG, "Error setting up SSL", e);
        }
    }

    public void validatekeystore() {
        InputStream keystoreStream = null;
        try {
            // Load the .p12 file from res/raw using the resource ID
            keystoreStream = context.getResources().openRawResource(keystoreResId);
            if (keystoreStream == null) {
                Log.e(TAG, "Keystore resource stream is null");
                return;
            }
            Log.d(TAG, "Keystore resource stream opened successfully");

            KeyStore keyStore = KeyStore.getInstance("JKS");
           keyStore.load(keystoreStream,KEYSTORE_PASSWORD.toCharArray());
            Log.d(TAG, "KeyStore instance created");
            Path path = Paths.get("C:\\Users\\Jaydeep\\Desktop\\key\\keystore.p12");
            String keystorePath = path.toAbsolutePath().toString();
            try (FileInputStream fis = new FileInputStream("../res/raw/keystore.p12")) {
                keyStore.load(fis, KEYSTORE_PASSWORD.toCharArray());
            }

            keyStore.load(keystoreStream, KEYSTORE_PASSWORD.toCharArray());
            Log.d(TAG, "Keystore loaded successfully");

            // Initialize KeyManagerFactory with the .p12 file
//            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//            keyManagerFactory.init(keyStore, KEYSTORE_PASSWORD.toCharArray());
//            Log.d(TAG, "KeyManagerFactory initialized");
//
//            // Initialize TrustManagerFactory with the .p12 file
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            trustManagerFactory.init(keyStore);
//            Log.d(TAG, "TrustManagerFactory initialized");
//
//            // Create TrustManager
//            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
//            final X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
//
//            // Initialize SSLContext with KeyManagerFactory and TrustManagerFactory
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagers, null);
//            Log.d(TAG, "SSLContext initialized");
//
//            OkHttpClient client = new OkHttpClient.Builder()
//                    .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
//                    .readTimeout(0, TimeUnit.MILLISECONDS)
//                    .build();
//
//            Request request = new Request.Builder()
//                    .url("wss://172.26.64.1:8080")
//                    .build();
//            webSocket = client.newWebSocket(request, this);

//            client.dispatcher().executorService().shutdown();
        } catch (Exception e) {
            Log.e(TAG, "Error setting up SSL", e);
        } finally {
            if (keystoreStream != null) {
                try {
                    keystoreStream.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing keystore stream", e);
                }
            }
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        if (callback != null) {
            callback.onConnected();
        }
        webSocket.send("Hello...");
        webSocket.send("...World!");
        webSocket.send(ByteString.decodeHex("deadbeef"));
        webSocket.close(1000, "Goodbye, World!");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "MESSAGE: " + text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Log.d(TAG, "MESSAGE: " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, null);
        Log.d(TAG, "CLOSE: " + code + " " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(TAG, "Error", t);
    }

    // Interface for the connected event callback
    public interface WebSocketListenerCallback {
        void onConnected();
    }
}
