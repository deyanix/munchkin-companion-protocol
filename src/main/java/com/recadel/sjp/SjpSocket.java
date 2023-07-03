package com.recadel.sjp;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class SjpSocket {
	private static final int BUFFER_SIZE = 1024;
	private final Socket socket;
	private SjpMessage currentMessage;
	private List<SjpSocketListener> listeners = new ArrayList<SjpSocketListener>();

	public SjpSocket(Socket socket) {
		this.socket = socket;
		this.currentMessage = new SjpMessage();
	}

	public void addListener(SjpSocketListener listener) {
		listeners.add(listener);
	}

	public void removeListener(SjpSocketListener listener) {
		listeners.remove(listener);
	}

	public void send(SjpMessage message) throws SjpException, IOException {
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
					System.out.println("Received ");
					receiveData(new SjpMessage(data).slice(0, length - 1));
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

	private void receiveData(SjpMessage message) {
		int offset = 0;
		while (offset < message.getLength() - 1) {
			int endIndex = message.indexOf(SjpMessage.END_OF_MESSAGE, offset);
			if (endIndex < 0) {
				endIndex = message.getLength() - 1;
			} else {
				endIndex += SjpMessage.END_OF_MESSAGE.getLength() - 1;
			}

			currentMessage = currentMessage.append(message.slice(offset, endIndex));
			offset += endIndex;
			if (currentMessage.isValid()) {
				listeners.forEach(listener -> listener.onMessage(currentMessage));
				currentMessage = new SjpMessage();
			} else {
				listeners.forEach(SjpSocketListener::onError);
			}
		}
	}
}
