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

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.util.Base64;

public class WebSocketHelper {

    private static final String TAG = "WebSocketHelper";
    private Context context;

    public WebSocketHelper(Context context) {
        this.context = context;
    }

    public void connectWebSocket() {
        try {
            Security.addProvider(new BouncyCastleProvider());

            // Load CA certificate
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = context.getResources().openRawResource(R.raw.ca);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                Log.d(TAG, "CA Certificate loaded.");
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            // Load client certificate
            InputStream crtInput = context.getResources().openRawResource(R.raw.crt);
            Certificate crt;
            try {
                crt = cf.generateCertificate(crtInput);
                Log.d(TAG, "Client Certificate loaded.");
            } finally {
                crtInput.close();
            }

            // Load client private key
            InputStream keyInput = context.getResources().openRawResource(R.raw.key);
            PrivateKey privateKey;
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(keyInput));
                StringBuilder keyBuilder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith("-----")) {
                        keyBuilder.append(line);
                    }
                }
                byte[] keyBytes = Base64.getDecoder().decode(keyBuilder.toString());
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                privateKey = keyFactory.generatePrivate(keySpec);
                Log.d(TAG, "Client Private Key loaded.");
            } finally {
                keyInput.close();
            }

            // Create a KeyStore for the client certificate and private key
            KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            clientKeyStore.load(null, null);
            clientKeyStore.setCertificateEntry("crt", crt);
            clientKeyStore.setKeyEntry("key", privateKey, "123".toCharArray(), new Certificate[]{crt});

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientKeyStore, "123".toCharArray()); // Use the password you used to generate the key

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            // Create OkHttpClient and configure it to use the SSLContext
            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) tmf.getTrustManagers()[0])
                    .protocols(Arrays.asList(okhttp3.Protocol.HTTP_1_1)) // Ensure HTTP/1.1 is used
                    .build();

            Request request = new Request.Builder().url("ws://www.wslphone.com:8080").build();
            WebSocketListener listener = new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    webSocket.send("hi");
                    Log.d(TAG, "WebSocket opened.");
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    Log.d(TAG, "Received: " + text);
                }

                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {
                    Log.d(TAG, "Received bytes: " + bytes.hex());
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    webSocket.close(1000, null);
                    Log.d(TAG, "Closing: " + code + " / " + reason);
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    t.printStackTrace();
                    Log.d(TAG, "WebSocket Failure: " + t.toString());
                }
            };

            WebSocket ws = client.newWebSocket(request, listener);
//            client.dispatcher().executorService().shutdown();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "WebSocketHelper: " + e.toString());
        }
    }
}
