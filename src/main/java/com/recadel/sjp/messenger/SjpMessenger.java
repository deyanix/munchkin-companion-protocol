package com.recadel.sjp.messenger;

import com.recadel.sjp.common.SjpMessage;
import com.recadel.sjp.common.SjpMessageBuffer;
import com.recadel.sjp.exception.SjpException;
import com.recadel.sjp.socket.SjpSocket;
import com.recadel.sjp.socket.SjpSocketListener;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class SjpMessenger  {
    private final long id;
    private final SjpSocket socket;
    private final List<SjpMessengerReceiver> receivers = new ArrayList<>();
    private long nextRequestId = 1;
    private ScheduledExecutorService executorService;

    public SjpMessenger(SjpSocket socket, long id) {
        this.socket = socket;
        this.id = id;
        socket.addListener(new SjpMessengerListener());
    }

    public SjpMessenger(SjpSocket socket) {
        this(socket, 0);
    }

    public void emit(String action, Object data) {
        try {
            socket.send(SjpMessage.createEvent(action, data).toBuffer());
        } catch (JSONException | IOException ex) {
            throw new SjpException("Error emitting event", ex);
        }
    }

    public void emit(String action) {
        emit(action, null);
    }

    public void request(String action, Object data) {
        try {
            socket.send(SjpMessage.createRequest(action, nextRequestId++, data).toBuffer());
        } catch (JSONException | IOException ex) {
            throw new SjpException("Error requesting", ex);
        }
    }

    public void addReceiver(SjpMessengerReceiver receiver) {
        receivers.add(receiver);
    }

    public long getId() {
        return id;
    }

    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    private void handleException(Throwable ex) {
        receivers.forEach(listener -> listener.onError(ex));
    }

    class SjpMessengerListener implements SjpSocketListener {
        @Override
        public void onMessage(SjpMessageBuffer buffer) {
            try {
                SjpMessage message = SjpMessage.fromBuffer(buffer);
                String action = message.getAction();
                Object data = message.getData();
				switch (message.getType()) {
					case EVENT -> receivers.forEach(receiver -> receiver.onEvent(action, data));
					case REQUEST -> receivers.forEach(receiver -> receiver.onRequest(action, data));
                    // TODO: Implement RESPONSE
				}
            } catch (JSONException e) {
                throw new SjpException(e);
            }
        }

        @Override
        public void onError(Throwable ex) {
            handleException(ex);
        }

        @Override
        public void onClose() {
            receivers.forEach(SjpMessengerReceiver::onClose);
        }
    }
}
