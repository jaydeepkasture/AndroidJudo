package com.example.imagemovement;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLHelper {

    private static final String TAG = "SSLHelper";

    public static SSLContext getSSLContext(Context context) {
        try {
            // Add BouncyCastle as a security provider
            if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
                Security.addProvider(new BouncyCastleProvider());
            }

            // Load the PEM file from res/raw using the resource ID
            Resources res = context.getResources();
            InputStream pemStream = res.openRawResource(R.raw.pem); // combined.pem
            Log.d(TAG, "Opened PEM input stream");

            // Read the input stream into a byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = pemStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            byte[] pemBytes = byteArrayOutputStream.toByteArray();
            pemStream.close();
            byteArrayOutputStream.close();

            // Read certificate and private key from PEM file
            Collection<X509Certificate> certificates = new ArrayList<>();
            PrivateKey privateKey = null;
            String pemString = new String(pemBytes);
            String[] tokens = pemString.split("-----END CERTIFICATE-----");
            for (String token : tokens) {
                token = token.trim();
                if (token.contains("CERTIFICATE")) {
                    token = token + "-----END CERTIFICATE-----";
                    InputStream certStream = new ByteArrayInputStream(token.getBytes());
                    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                    X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(certStream);
                    certificates.add(certificate);
                } else if (token.contains("PRIVATE KEY")) {
                    token = token.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", "");
                    byte[] keyBytes = Base64.getDecoder().decode(token);
                    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    privateKey = keyFactory.generatePrivate(keySpec);
                }
            }

            if (privateKey == null || certificates.isEmpty()) {
                throw new IllegalStateException("Failed to load private key and certificates from PEM file");
            }

            // Initialize KeyStore with the certificates and private key
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            int i = 0;
            for (X509Certificate certificate : certificates) {
                keyStore.setCertificateEntry("cert-" + i++, certificate);
            }
            keyStore.setKeyEntry("key", privateKey, null, certificates.toArray(new X509Certificate[0]));

            // Initialize KeyManagerFactory with the KeyStore
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, null);
            Log.d(TAG, "Initialized KeyManagerFactory");

            // Initialize TrustManagerFactory with the KeyStore
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            Log.d(TAG, "Initialized TrustManagerFactory");

            // Create TrustManager
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

            // Initialize SSLContext with KeyManagerFactory and TrustManagerFactory
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagers, null);
            Log.d(TAG, "Initialized SSLContext");

            return sslContext;
        } catch (Exception e) {
            Log.e(TAG, "Error initializing SSL context", e);
            throw new RuntimeException("Error initializing SSL context", e);
        }
    }
}
