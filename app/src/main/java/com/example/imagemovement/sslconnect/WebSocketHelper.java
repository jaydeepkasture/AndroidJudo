package com.example.imagemovement.sslconnect;

import android.content.Context;
import android.util.Log;

import com.example.imagemovement.R;

import okhttp3.HttpUrl;
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
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.tls.Certificates;
import okhttp3.tls.HandshakeCertificates;
public class WebSocketHelper {

    private static final String TAG = "WebSocketHelper";
    private Context context;
    private ExecutorService executorService;

    public WebSocketHelper(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
    }
    private String inputStreamToString(InputStream inputStream) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append('\n');
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }
    public void connectWebSocket() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    InputStream letsEncryptInput = context.getResources().openRawResource(R.raw.crt);
                    X509Certificate letsEncryptCertificateAuthority = Certificates.decodeCertificatePem(inputStreamToString(letsEncryptInput));
                    letsEncryptInput.close();
                    Log.d(TAG, "Let's Encrypt Certificate loaded.");

                    // Load Entrust Root CA certificate
                    InputStream entrustInput = context.getResources().openRawResource(R.raw.ca);
                    X509Certificate entrustRootCertificateAuthority = Certificates.decodeCertificatePem(inputStreamToString(entrustInput));
                    entrustInput.close();
                    Log.d(TAG, "Entrust Root Certificate loaded.");

                    // Load Comodo RSA CA certificate
                    InputStream comodoInput = context.getResources().openRawResource(R.raw.pem);
                    X509Certificate comodoRsaCertificationAuthority = Certificates.decodeCertificatePem(inputStreamToString(comodoInput));
                    comodoInput.close();
                    Log.d(TAG, "Comodo RSA Certificate loaded.");
                    HandshakeCertificates certificates = new HandshakeCertificates.Builder()
                            .addTrustedCertificate(letsEncryptCertificateAuthority)
                            .addTrustedCertificate(entrustRootCertificateAuthority)
                            .addTrustedCertificate(comodoRsaCertificationAuthority)
                            .build();
                    OkHttpClient  client = new OkHttpClient.Builder()
                            .sslSocketFactory(certificates.sslSocketFactory(), certificates.trustManager())
                            .build();
                    // Build the URL using HttpUrl
                    HttpUrl url = new HttpUrl.Builder()
                            .scheme("http")  // Use https for initial URL creation
                            .host("www.wslphone.com")
                            .port(8080)
                            .build();
                    Request request = new Request.Builder().url(url).build();
                    WebSocketListener listener = new WebSocketListener() {
                        @Override
                        public void onOpen(WebSocket webSocket, Response response) {
                            webSocket.send("Hello, Server!");
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

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "WebSocketHelper: " + e.toString());
                }
            }
        });

    }
}
