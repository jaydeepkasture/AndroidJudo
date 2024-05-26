package com.example.imagemovement.sslconnect;
import android.os.AsyncTask;
import android.util.Log;
import java.net.InetAddress;

public class ConnectivityTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "ConnectivityCheck";

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            InetAddress address = InetAddress.getByName("www.wslphone.com");
            Log.d(TAG, "IP Address: " + address.getHostAddress());
            // Try to ping the server
            boolean reachable = address.isReachable(5000); // timeout in milliseconds
            Log.d(TAG, "Reachable: " + reachable);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error: " + e.toString());
        }
        return null;
    }
}
