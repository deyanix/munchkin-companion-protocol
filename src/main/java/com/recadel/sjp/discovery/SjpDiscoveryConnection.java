package com.recadel.sjp.discovery;

import com.recadel.sjp.connection.SjpMessage;
import com.recadel.sjp.connection.SjpMessageBuffer;
import com.recadel.sjp.connection.SjpMessagePattern;
import com.recadel.sjp.connection.SjpMessageType;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public abstract class SjpDiscoveryConnection {
	protected static final SjpMessagePattern WELCOME_REQUEST_PATTERN = new SjpMessagePattern(SjpMessageType.REQUEST, "welcome", "look-for-trouble");
	protected static final SjpMessagePattern WELCOME_RESPONSE_PATTERN = new SjpMessagePattern(SjpMessageType.RESPONSE, "welcome", "wandering-monster");
	protected final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
	protected final DatagramSocket socket;
	private final Map<SocketAddress, SjpDiscoveryReceiver> receivers = new ConcurrentHashMap<>();
	private int datagramLength = 1024;
	private long receiverLifetime = 5000L;

	protected SjpDiscoveryConnection(SocketAddress address) throws SocketException {
		socket = new DatagramSocket(address);
	}

	protected SjpDiscoveryConnection() throws SocketException {
		socket = new DatagramSocket();
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
		executorService.shutdown();
	}

	protected void receive(BiConsumer<SocketAddress, SjpMessage> consumer) {
		executorService.submit(() -> {
			byte[] buffer = new byte[datagramLength];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			while (!executorService.isShutdown()) {
				try {
					socket.receive(packet);
					handlePacket(packet, consumer);
				} catch (IOException ignored) {
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
		SjpDiscoveryReceiver receiver;
		if (!receivers.containsKey(address)) {
			receiver = new SjpDiscoveryReceiver();
			receivers.put(address, receiver);
		} else {
			receiver = receivers.get(address);
		}

		receiver.receive(SjpMessageBuffer.fromDatagramPacket(packet))
				.ifPresent(buffer -> consumer.accept(address, SjpMessage.fromBuffer(buffer)));
	}
}
