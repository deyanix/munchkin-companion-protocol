package com.recadel.sjp.connection;

import com.recadel.sjp.exception.SjpException;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SjpSocket {
	private static final int BUFFER_SIZE = 1024;
	private final Socket socket;
	private final List<SjpSocketListener> listeners = new ArrayList<>();
	private final BlockingQueue<SjpMessageBuffer> buffers = new LinkedBlockingQueue<>();
	private final ExecutorService executorService;
	private SjpMessageBuffer currentMessage;

	public SjpSocket(Socket socket, ExecutorService executorService) {
		this.socket = socket;
		this.currentMessage = new SjpMessageBuffer();
		this.executorService = executorService;
	}

	public SjpSocket(Socket socket) {
		this(socket, Executors.newCachedThreadPool());
	}

	public void close() throws IOException {
		socket.close();
		executorService.shutdown();
		buffers.add(new SjpMessageBuffer());
		listeners.forEach(SjpSocketListener::onClose);
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
		executorService.submit(() -> {
			try {
				InputStream input = socket.getInputStream();
				byte[] data = new byte[BUFFER_SIZE];
				int length;
				while (!executorService.isShutdown() && (length = input.read(data)) != -1) {
					buffers.add(new SjpMessageBuffer(data).slice(0, length - 1));
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
		executorService.submit(() -> {
			while (!executorService.isShutdown()) {
				try {
					SjpMessageBuffer buffer = currentMessage.isEmpty() ?
								buffers.take() :
								buffers.poll(5, TimeUnit.SECONDS);

					if (buffer == null) {
						currentMessage = new SjpMessageBuffer();
						listeners.forEach(SjpSocketListener::onError);
						continue;
					}
					receiveData(buffer);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});
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
