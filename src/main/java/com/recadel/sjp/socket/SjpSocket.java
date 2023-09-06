package com.recadel.sjp.socket;

import com.recadel.sjp.common.SjpMessageBuffer;
import com.recadel.sjp.common.SjpReceiver;
import com.recadel.sjp.common.SjpReceiverGarbageCollector;
import com.recadel.sjp.exception.SjpException;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class SjpSocket {
	private static final int BUFFER_SIZE = 1024;
	private final Socket socket;
	private final List<SjpSocketListener> listeners = new ArrayList<>();
	private final SjpReceiver receiver = new SjpReceiver();
	private SjpReceiverGarbageCollector garbageCollector;
	private boolean emittedClose = false;

	public SjpSocket(Socket socket) {
		this.socket = socket;
	}

	public void close() throws IOException {
		socket.close();
	}

	public void addListener(SjpSocketListener listener) {
		listeners.add(listener);
	}

	public void removeListener(SjpSocketListener listener) {
		listeners.remove(listener);
	}

	public void applyGarbageCollector(SjpReceiverGarbageCollector garbageCollector) {
		detachGarbageCollector();
		this.garbageCollector = garbageCollector;
		garbageCollector.registerReceiver(receiver);
	}

	public void detachGarbageCollector() {
		if (garbageCollector != null) {
			garbageCollector.unregisterReceiver(receiver);
		}
	}

	public void send(SjpMessageBuffer message) throws SjpException, IOException {
		if (!message.isValid()) {
			throw new SjpException("Cannot send a invalid message");
		}
		try {
			System.out.println("[NATIVE] Sending");
			socket.getOutputStream().write(message.getBuffer());
			socket.getOutputStream().flush();
		} catch (IOException ex) {
			handleException(ex);
			tryClose();
		}
	}

	public void setup(ScheduledExecutorService executorService) {
		if (garbageCollector != null) {
			garbageCollector.start();
		}
		executorService.submit(() -> {
			try {
				InputStream input = socket.getInputStream();
				byte[] data = new byte[BUFFER_SIZE];
				int length;
				while (!executorService.isShutdown() && (length = input.read(data)) != -1) {
					receiveData(new SjpMessageBuffer(data).slice(0, length - 1));
				}
			} catch (IOException ex) {
				handleException(ex);
				tryClose();
			}
		});
	}

	private void receiveData(SjpMessageBuffer buffer) {
		receiver.receiveAll(buffer)
				.forEach(message ->
						listeners.forEach(listener -> listener.onMessage(message)));
	}

	private void tryClose() {
		emitCloseEvent();
		if (socket.isClosed()) {
			return;
		}

		try {
			close();
		} catch (IOException ex) {
			handleException(ex);
		}
	}

	private void emitCloseEvent() {
		if (emittedClose) {
			return;
		}
		emittedClose = true;
		listeners.forEach(SjpSocketListener::onClose);
	}

	private void handleException(Exception ex) {
		listeners.forEach(listener -> listener.onError(ex));
		ex.printStackTrace();
	}
}
