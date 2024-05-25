package com.example.imagemovement;
import android.content.Context;
import android.util.Log;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Security;

import javax.net.ssl.KeyManagerFactory;

public class KeystoreLoader {
    private static final String TAG = "KeystoreLoader";
    private static final String KEYSTORE_PASSWORD = "123";

    public static KeyStore loadKeystore(Context context, int keystoreResId) {
        try {
            InputStream keystoreStream = context.getResources().openRawResource(keystoreResId);
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            String keyManagerFactoryAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(keyManagerFactoryAlgorithm);
            ByteArrayInputStream stream =convertToByteArrayInputStream(keystoreStream);

            keyStore.load(stream, KEYSTORE_PASSWORD.toCharArray());
            Security.addProvider(new BouncyCastleProvider());

            if (keystoreStream == null) {
                Log.e(TAG, "Keystore resource stream is null");
                return null;
            }
            Log.d(TAG, "Keystore resource stream opened successfully");
//            KeyStore keystore = KeystoreLoader.loadKeystore(context, R.raw.keystore);

            keyStore.load(keystoreStream, KEYSTORE_PASSWORD.toCharArray());
            Log.d(TAG, "Keystore loaded successfully");

            return keyStore;
        } catch (Exception e) {
            Log.e(TAG, "Error loading keystore", e);
            return null;
        }
    }
    public static ByteArrayInputStream convertToByteArrayInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return new ByteArrayInputStream(buffer.toByteArray());
    }
}
