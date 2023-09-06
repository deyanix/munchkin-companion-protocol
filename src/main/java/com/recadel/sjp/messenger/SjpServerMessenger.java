package com.recadel.sjp.messenger;

import com.recadel.sjp.common.SjpReceiverGarbageCollector;
import com.recadel.sjp.socket.SjpSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class SjpServerMessenger {
    private final ServerSocket serverSocket;
    private final List<SjpMessenger> messengers = new ArrayList<>();
    private final List<SjpServerMessengerListener> listeners = new ArrayList<>();
    private SjpReceiverGarbageCollector garbageCollector;
    private long nextMessengerId = 0;

    public SjpServerMessenger(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void addListener(SjpServerMessengerListener listener) {
        listeners.add(listener);
    }

    public void start(ScheduledExecutorService executorService) {
        executorService.execute(() -> {
            while (!serverSocket.isClosed() && !executorService.isShutdown()) {
                try {
                    Socket socket = serverSocket.accept();
                    SjpSocket sjpSocket = new SjpSocket(socket);
                    sjpSocket.applyGarbageCollector(garbageCollector);
                    sjpSocket.setup(executorService);

                    SjpMessenger messenger = new SjpMessenger(sjpSocket, nextMessengerId++);
                    messengers.add(messenger);
                    listeners.forEach(listener -> listener.onConnect(messenger));
                } catch (IOException ex) {
                    listeners.forEach(listener -> listener.onError(ex));
                }
            }
            listeners.forEach(SjpServerMessengerListener::onClose);
        });
    }

    public void broadcast(String action, Object data) {
        messengers.forEach(messenger -> messenger.emit(action, data));
    }

    public void broadcastExcept(String action, Object data, int exceptMessengerId) {
        messengers.stream()
                .filter(messenger -> messenger.getId() != exceptMessengerId)
                .forEach(messenger -> messenger.emit(action, data));
    }

    public SjpReceiverGarbageCollector getGarbageCollector() {
        return garbageCollector;
    }

    public void setGarbageCollector(SjpReceiverGarbageCollector garbageCollector) {
        this.garbageCollector = garbageCollector;
    }
}
