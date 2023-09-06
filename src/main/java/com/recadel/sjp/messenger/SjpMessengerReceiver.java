package com.recadel.sjp.messenger;

public interface SjpMessengerReceiver {
    void onEvent(String action, Object data);
    void onRequest(String action, Object data);
}
