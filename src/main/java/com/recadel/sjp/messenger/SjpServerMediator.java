package com.recadel.sjp.messenger;

import com.recadel.sjp.common.SjpReceiverGarbageCollector;
import com.recadel.sjp.socket.SjpSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class SjpServerMediator {
    private final ServerSocket serverSocket;
    private final List<SjpMessenger> messengers = new ArrayList<>();
    private final List<SjpServerMessengerListener> listeners = new ArrayList<>();
    private SjpReceiverGarbageCollector garbageCollector;
    private long nextMessengerId = 0;

    public SjpServerMediator(ServerSocket serverSocket) {
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
                    messenger.addReceiver(new SjpServerMediatorReceiver(messenger));
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

    public void broadcastExcept(String action, Object data, long exceptMessengerId) {
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

    class SjpServerMediatorReceiver implements SjpMessengerReceiver {
        private final SjpMessenger messenger;

        SjpServerMediatorReceiver(SjpMessenger messenger) {
            this.messenger = messenger;
        }

        @Override
        public void onEvent(String action, Object data) {
        }

        @Override
        public void onRequest(String action, Object data) {
        }

        @Override
        public void onError(Throwable ex) {
        }

        @Override
        public void onClose() {
            messengers.remove(messenger);
        }
    }
}
