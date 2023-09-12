package com.recadel.sjp.discovery;

import com.recadel.sjp.common.SjpMessage;
import com.recadel.sjp.common.SjpMessageBuffer;
import com.recadel.sjp.common.SjpMessagePattern;
import com.recadel.sjp.common.SjpMessageType;
import com.recadel.sjp.common.SjpReceiver;
import org.json.JSONException;

import java.io.Closeable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public abstract class SjpDiscoveryConnection implements Closeable {
	protected static final SjpMessagePattern WELCOME_REQUEST_PATTERN = new SjpMessagePattern(SjpMessageType.REQUEST, "welcome", "look-for-trouble");
	protected static final SjpMessagePattern WELCOME_RESPONSE_PATTERN = new SjpMessagePattern(SjpMessageType.RESPONSE, "welcome", "wandering-monster");
	protected final DatagramSocket socket;
	private final Map<SocketAddress, SjpReceiver> receivers = new ConcurrentHashMap<>();
	private int datagramLength = 1024;
	private long receiverLifetime = 5000L;

	protected SjpDiscoveryConnection(DatagramSocket socket) {
		this.socket = socket;
	}

	public int getDatagramLength() {
		return datagramLength;
	}

	public void setDatagramLength(int datagramLength) {
		this.datagramLength = datagramLength;
	}

	public long getReceiverLifetime() {
		return receiverLifetime;
	}

	public void setReceiverLifetime(long receiverLifetime) {
		this.receiverLifetime = receiverLifetime;
	}

	public void close() {
		socket.close();
	}

	protected void receive(BiConsumer<SocketAddress, SjpMessage> consumer, ScheduledExecutorService executorService) {
		executorService.submit(() -> {
			byte[] buffer = new byte[datagramLength];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			while (!socket.isClosed() && !executorService.isShutdown()) {
				try {
					socket.receive(packet);
					handlePacket(packet, consumer);
				} catch (Exception ignored) {
				}
			}
		});

		executorService.scheduleAtFixedRate(() -> receivers.entrySet()
				.parallelStream()
				.filter(entry ->
						entry.getValue().getLastReceivedBuffer().until(
								LocalDateTime.now(),
								ChronoUnit.MILLIS) > receiverLifetime)
				.map(Map.Entry::getKey)
				.forEach(receivers::remove), 0, receiverLifetime, TimeUnit.MILLISECONDS);
	}

	private void handlePacket(DatagramPacket packet, BiConsumer<SocketAddress, SjpMessage> consumer) {
		SocketAddress address = packet.getSocketAddress();
		SjpReceiver receiver = receivers.getOrDefault(address, null);
		if (receiver == null) {
			receiver = new SjpReceiver();
			receivers.put(address, receiver);
		}

		receiver.receiveAll(SjpMessageBuffer.fromDatagramPacket(packet))
				.forEach(buffer -> {
					try {
						consumer.accept(address, SjpMessage.fromBuffer(buffer));
					} catch (JSONException ignored) {
					}
				});
	}
}
