package com.example.imagemovement.sslconnect;import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.Response;
import okhttp3.Headers;
import okio.ByteString;

import android.content.Context;
import android.util.Log;
import java.io.IOException;

public class WebSocketClient {

    private OkHttpClient client;
    private WebSocket webSocket;
    private static final String TAG = "WebSocketClient";

    public WebSocketClient(Context con) {
        client = new OkHttpClient();
    }

    public void connect(String url) {
        Request request = new Request.Builder().url(url).build();
        webSocket = client.newWebSocket(request, new EchoWebSocketListener());
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Log.d(TAG, "WebSocket opened");
            webSocket.send("Hello, Server!");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.d(TAG, "Receiving: " + text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            Log.d(TAG, "Receiving bytes: " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            Log.d(TAG, "Closing: " + code + " / " + reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Log.d(TAG, "Closed: " + code + " / " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            if (response != null) {
                Log.e(TAG, "Error: " + response.message());
                handleError(response.code(), response.message());
            } else {
                Log.e(TAG, "Error: " + t.getMessage(), t);
                handleError(-1, t.getMessage());
            }
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Goodbye!");
        }
    }

    private void handleError(int errorCode, String errorMessage) {
        // Handle different types of errors based on errorCode
        switch (errorCode) {
            case 400:
                Log.e(TAG, "Bad Request: " + errorMessage);
                break;
            case 401:
                Log.e(TAG, "Unauthorized: " + errorMessage);
                break;
            case 403:
                Log.e(TAG, "Forbidden: " + errorMessage);
                break;
            case 404:
                Log.e(TAG, "Not Found: " + errorMessage);
                break;
            case 500:
                Log.e(TAG, "Internal Server Error: " + errorMessage);
                break;
            default:
                Log.e(TAG, "Unknown error occurred: " + errorMessage);
                break;
        }
    }
}
