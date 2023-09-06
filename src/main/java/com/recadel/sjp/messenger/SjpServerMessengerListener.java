package com.recadel.sjp.messenger;

public interface SjpServerMessengerListener {
    void onConnect(SjpMessenger messenger);
    void onClose();
    void onError(Throwable throwable);
}
