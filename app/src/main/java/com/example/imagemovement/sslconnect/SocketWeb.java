package com.example.imagemovement.sslconnect;


import android.util.Log;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public final class SocketWeb extends WebSocketListener {
    private String TAG="SocketWeb";
    private void run() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0,  TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url("https://8080-cs-4547aa50-445b-4280-a13d-57881ff8cebc.cs-asia-southeast1-bool.cloudshell.dev/?authuser=0&redirectedPreviously=true")
                .build();
        client.newWebSocket(request, this);

        // Trigger shutdown of the dispatcher's executor so this process exits immediately.
//        client.dispatcher().executorService().shutdown();
    }

    @Override public void onOpen(WebSocket webSocket, Response response) {
        webSocket.send("Hello...");
        webSocket.send("...World!");
        webSocket.send(ByteString.decodeHex("deadbeef"));
        webSocket.close(1000, "Goodbye, World!");
    }

    @Override public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG,"MESSAGE: " + text);
    }

    @Override public void onMessage(WebSocket webSocket, ByteString bytes) {
        Log.d(TAG,"MESSAGE: " + bytes.hex());
    }

    @Override public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, null);
        Log.d(TAG,"CLOSE: " + code + " " + reason);
    }

    @Override public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
        Log.d(TAG,t.toString());
    }

    public static void GetSocket() {
       new SocketWeb().run();
    }
}
