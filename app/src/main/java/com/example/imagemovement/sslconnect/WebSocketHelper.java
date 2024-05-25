package com.example.imagemovement.sslconnect;
import android.content.Context;
import android.util.Log;

import com.example.imagemovement.R;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class WebSocketHelper {

    private static final String TAG ="WebSocketHelper" ;
    private Context context;

    public WebSocketHelper(Context context) {
        this.context = context;
    }

    public void connectWebSocket() {
        try {
            // Load CA certificate
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = context.getResources().openRawResource(R.raw.ca);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream keyInputStream = context.getResources().openRawResource(R.raw.keystore);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            // Load client certificate and key
            InputStream crtInput = context.getResources().openRawResource(R.raw.crt);
            Certificate crt = cf.generateCertificate(crtInput);
            crtInput.close();

            InputStream keyInput = context.getResources().openRawResource(R.raw.key);
            // Assuming the key is in PKCS#8 format
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "123".toCharArray()); // Use the password you used to generate the key

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            // Create OkHttpClient and configure it to use the SSLContext
            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) tmf.getTrustManagers()[0])
                    .build();


            CustomTrust ct=new CustomTrust(context);
            client=ct.getClient();
            Request request = new Request.Builder().url("wss://www.wslphone.com:8080").build();
            WebSocketListener listener = new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    webSocket.send("hi");
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    Log.d(TAG,"Received: " + text);
                }

                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {
                    Log.d(TAG,"Received bytes: " + bytes.hex());
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    webSocket.close(1000, null);
                    Log.d(TAG,"Closing: " + code + " / " + reason);
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    t.printStackTrace();
                    Log.d(TAG,t.toString());
                }
            };

            WebSocket ws = client.newWebSocket(request, listener);
            client.dispatcher().executorService().shutdown();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "WebsocktHelper: " + e.toString());
        }
    }
}
