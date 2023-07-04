package com.recadel.sjp.connection;

import com.recadel.sjp.exception.SjpException;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SjpSocket {
	private static final int BUFFER_SIZE = 1024;
	private final Socket socket;
	private final List<SjpSocketListener> listeners = new ArrayList<>();
	private SjpMessageBuffer currentMessage;

	public SjpSocket(Socket socket) {
		this.socket = socket;
		this.currentMessage = new SjpMessageBuffer();
	}

	public void addListener(SjpSocketListener listener) {
		listeners.add(listener);
	}

	public void removeListener(SjpSocketListener listener) {
		listeners.remove(listener);
	}

	public void send(SjpMessageBuffer message) throws SjpException, IOException {
		if (!message.isValid()) {
			throw new SjpException("Cannot send a invalid message");
		}
		socket.getOutputStream().write(message.getBuffer());
		socket.getOutputStream().flush();
	}

	public void setup() {
		new Thread(() -> {
			try {
				InputStream input = socket.getInputStream();
				byte[] data = new byte[BUFFER_SIZE];
				int length;
				while ((length = input.read(data)) != -1) {
					receiveData(new SjpMessageBuffer(data).slice(0, length - 1));
				}
			} catch (IOException ex) {
				listeners.forEach(SjpSocketListener::onError);
				ex.printStackTrace();
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} finally {
				if (socket.isClosed()) {
					listeners.forEach(SjpSocketListener::onClose);
				}
			}
		}).start();
	}

	private void receiveData(SjpMessageBuffer message) {
		int offset = 0;
		while (offset < message.getLength() - 1) {
			int endIndex = message.indexOf(SjpMessageBuffer.END_OF_MESSAGE, offset);
			if (endIndex < 0) {
				endIndex = message.getLength() - 1;
			} else {
				endIndex += SjpMessageBuffer.END_OF_MESSAGE.getLength() - 1;
			}

			currentMessage = currentMessage.append(message.slice(offset, endIndex));
			offset += endIndex;
			if (currentMessage.isValid()) {
				listeners.forEach(listener -> listener.onMessage(currentMessage));
				currentMessage = new SjpMessageBuffer();
			} else {
				listeners.forEach(SjpSocketListener::onError);
			}
		}
	}
}
