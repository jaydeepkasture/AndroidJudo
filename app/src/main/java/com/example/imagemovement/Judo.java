package com.example.imagemovement;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.imagemovement.sslconnect.WebSocketHelper;

public class Judo extends AppCompatActivity {

    private static final String TAG = "Judo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
//            SocketWeb.GetSocket();
           WebSocketHelper webSocketHelper = new WebSocketHelper(this);
            webSocketHelper.connectWebSocket();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error: " + e.toString());
        }
    }
}
