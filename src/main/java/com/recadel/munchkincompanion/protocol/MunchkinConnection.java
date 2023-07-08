package com.recadel.munchkincompanion.protocol;

import com.recadel.sjp.common.SjpMessage;
import com.recadel.sjp.common.SjpMessageBuffer;
import com.recadel.sjp.common.SjpMessageType;
import com.recadel.sjp.connection.SjpAbstractSocketListener;
import com.recadel.sjp.connection.SjpSocket;
import com.recadel.sjp.exception.SjpException;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class MunchkinConnection extends SjpAbstractSocketListener {
	private static final long TIMEOUT = 5000L;
	private final long id;
	private final SjpSocket socket;
	private long nextMessageId = 1;
	private final Map<Long, CompletableFuture<SjpMessage>> futureMap = new ConcurrentHashMap<>();

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

	public long request(String action, Object data) throws SjpException, IOException {
		return request(action, nextMessageId++, data);
	}

	public long request(String action, long id, Object data) throws SjpException, IOException {
		socket.send(SjpMessage.createRequest(action, id, data).toBuffer());
		return id;
	}

	public CompletableFuture<Object> requestAndWait(String action, Object data) throws SjpException, IOException {
		long id = nextMessageId++;
		CompletableFuture<SjpMessage> future = CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(TIMEOUT);
				futureMap.remove(id);
				throw new RuntimeException("Timeout");
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});

		request(action, id, data);
		futureMap.put(id, future);
		return future.thenApply(SjpMessage::getData);
	}

	public void emit(String action, Object data) throws SjpException, IOException {
		socket.send(SjpMessage.createEvent(action, data).toBuffer());
	}

	@Override
	public void onMessage(SjpMessageBuffer buffer) {
		SjpMessage message = SjpMessage.fromBuffer(buffer);

		if (message.getType() == SjpMessageType.REQUEST && message.getAction().equals("join")) {
			try {
				socket.send(SjpMessage.createResponse(message.getId(), "It is my response!").toBuffer());
			} catch (SjpException | IOException e) {
				throw new RuntimeException(e);
			}
		} else if (message.getType() == SjpMessageType.RESPONSE) {
			CompletableFuture<SjpMessage> future = futureMap.getOrDefault(message.getId(), null);
			if (future == null) {
				throw new RuntimeException("Unknown request");
			}
			future.complete(message);
		}
	}
}
