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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SjpSocket {
	private static final int BUFFER_SIZE = 1024;
	private final Socket socket;
	private final List<SjpSocketListener> listeners = new ArrayList<>();
	private final SjpReceiver receiver = new SjpReceiver();
	private final ScheduledExecutorService executorService;
	private SjpReceiverGarbageCollector garbageCollector;

	public SjpSocket(Socket socket, ScheduledExecutorService executorService) {
		this.socket = socket;
		this.executorService = executorService;
	}

	public SjpSocket(Socket socket) {
		this(socket, Executors.newScheduledThreadPool(2));
	}

	public void close() throws IOException {
		socket.close();
		executorService.shutdown();
		listeners.forEach(SjpSocketListener::onClose);
	}

	public void addListener(SjpSocketListener listener) {
		listeners.add(listener);
	}

	public void removeListener(SjpSocketListener listener) {
		listeners.remove(listener);
	}

	public void applyGarbageCollector(SjpReceiverGarbageCollector garbageCollector) {
		stopGarbageCollector();
		this.garbageCollector = garbageCollector;
		garbageCollector.registerReceiver(receiver);
	}

	public void stopGarbageCollector() {
		if (garbageCollector != null) {
			garbageCollector.unregisterReceiver(receiver);
		}
	}

	public void send(SjpMessageBuffer message) throws SjpException, IOException {
		if (!message.isValid()) {
			throw new SjpException("Cannot send a invalid message");
		}
		socket.getOutputStream().write(message.getBuffer());
		socket.getOutputStream().flush();
	}

	public void setup() {
		executorService.submit(() -> {
			try {
				InputStream input = socket.getInputStream();
				byte[] data = new byte[BUFFER_SIZE];
				int length;
				while (!executorService.isShutdown() && (length = input.read(data)) != -1) {
					receiveData(new SjpMessageBuffer(data).slice(0, length - 1));
				}
			} catch (IOException ex) {
				listeners.forEach(SjpSocketListener::onError);
				ex.printStackTrace();
				try {
					close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void receiveData(SjpMessageBuffer buffer) {
		receiver.receiveAll(buffer)
				.forEach(message ->
						listeners.forEach(listener -> listener.onMessage(message)));
	}
}
