package com.example.imagemovement.sslconnect;
import android.content.Context;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class KeyStoreUtil {

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        return buffer.toByteArray();
    }

    private static PrivateKey loadPrivateKey(InputStream keyInputStream) throws Exception {
        byte[] keyBytes = readAllBytes(keyInputStream);
        String privateKeyPEM = new String(keyBytes)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decodedKey = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private static X509Certificate loadCertificate(InputStream certInputStream) throws Exception {
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certFactory.generateCertificate(certInputStream);
    }

    public static KeyStore createKeyStore(Context context, int keyResId, int certResId, String keystorePassword) throws Exception {
        // Load private key
        PrivateKey privateKey;
        try (InputStream keyInputStream = context.getResources().openRawResource(keyResId)) {
            privateKey = loadPrivateKey(keyInputStream);
        }

        // Load certificate
        X509Certificate certificate;
        try (InputStream certInputStream = context.getResources().openRawResource(certResId)) {
            certificate = loadCertificate(certInputStream);
        }

        // Create KeyStore and add private key and certificate
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);
        keyStore.setKeyEntry("alias", privateKey, keystorePassword.toCharArray(), new X509Certificate[]{certificate});

        return keyStore;
    }
}
