package com.recadel.munchkincompanion.protocol;

import com.recadel.sjp.connection.SjpAbstractSocketListener;
import com.recadel.sjp.connection.SjpMessage;
import com.recadel.sjp.connection.SjpMessageBuffer;
import com.recadel.sjp.connection.SjpMessageType;
import com.recadel.sjp.connection.SjpSocket;
import com.recadel.sjp.exception.SjpException;

import java.io.IOException;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class MunchkinConnection extends SjpAbstractSocketListener {
	private final long id;
	private final SjpSocket socket;
	private int nextMessageId = 1;
	private final BlockingQueue<SjpMessage> messages = new LinkedBlockingQueue<>();

	public MunchkinConnection(long id, SjpSocket socket) {
		this.id = id;
		this.socket = socket;
		this.socket.addListener(this);
	}

	public MunchkinConnection(SjpSocket socket) {
		this(-1, socket);
	}

	public long getId() {
		return id;
	}

	public SjpSocket getSocket() {
		return socket;
	}

	public int request(String action, Object data) throws SjpException, IOException {
		int id = nextMessageId++;
		socket.send(SjpMessage.createRequest(action, id, data).toBuffer());
		return id;
	}

	public Future<Object> requestAsync(String action, Object data) throws SjpException, IOException {
		final int id = request(action, data);
		return CompletableFuture.supplyAsync(() -> {
			try {
				while (true) {
					SjpMessage message = messages.take();
					if (Objects.equals(message.getId(), id)) {
						return message.getData();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		});
	}

	public void emit(String action, Object data) throws SjpException, IOException {
		socket.send(SjpMessage.createEvent(action, data).toBuffer());
	}

	@Override
	public void onMessage(SjpMessageBuffer buffer) {
		SjpMessage message = SjpMessage.fromBuffer(buffer);
		System.out.printf("Received %s (action: %s, id: %d, data: %s)\r\n", message.getType(), message.getAction(), message.getId(), message.getData());
		messages.add(message);

		if (message.getType() == SjpMessageType.REQUEST) {
			try {
				socket.send(SjpMessage.createResponse(message.getId(), "It is my response!").toBuffer());
			} catch (SjpException | IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
