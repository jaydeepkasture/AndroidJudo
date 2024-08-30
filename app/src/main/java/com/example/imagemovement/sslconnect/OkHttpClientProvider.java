package com.example.imagemovement.sslconnect;
import okhttp3.OkHttpClient;
import okhttp3.tls.HandshakeCertificates;

import java.security.cert.X509Certificate;

public class OkHttpClientProvider {

    public static OkHttpClient getOkHttpClient() {
        // Trust all certificates (for development use only)
        HandshakeCertificates certificates = new HandshakeCertificates.Builder()
                .addPlatformTrustedCertificates()
                .addInsecureHost("yourserver.com") // For self-signed certificates
                .build();

        return new OkHttpClient.Builder()
                .sslSocketFactory(certificates.sslSocketFactory(), certificates.trustManager())
                .build();
    }
}
