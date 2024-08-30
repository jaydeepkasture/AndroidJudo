//package com.example.imagemovement.sslconnect;
//import android.content.Context;
//
//import com.example.imagemovement.R;
//
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import java.io.InputStream;
//import java.security.KeyStore;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.TrustManagerFactory;
//import okhttp3.logging.HttpLoggingInterceptor;
//
//public class HttpClientProvider {
//
//    public OkHttpClient getOkHttpClient(Context context) {
//        try {
//            // Load PFX file
//            InputStream keyStoreStream = context.getResources().openRawResource(R.raw.); // Use your actual PFX file location
//            KeyStore keyStore = KeyStore.getInstance("PKCS12");
//            keyStore.load(keyStoreStream, "yourpassword".toCharArray());
//
//            // Initialize TrustManagerFactory with the read PFX
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            trustManagerFactory.init(keyStore);
//
//            // Initialize SSLContext with the TrustManagerFactory
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
//
//            // Build OkHttpClient with the SSLContext
//            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
//                    .sslSocketFactory(sslContext.getSocketFactory());
//
//            // Add logging for debugging (optional)
//            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//            clientBuilder.addInterceptor(logging);
//
//            return clientBuilder.build();
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
