package com.recadel.sjp.discovery;

import com.recadel.sjp.connection.SjpMessage;
import com.recadel.sjp.connection.SjpMessageBuffer;
import com.recadel.sjp.connection.SjpMessagePattern;
import com.recadel.sjp.connection.SjpMessageType;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SjpDiscovery {
	private static final SjpMessagePattern WELCOME_REQUEST_PATTERN = new SjpMessagePattern(SjpMessageType.REQUEST, "welcome", "look-for-trouble");
	private static final SjpMessagePattern WELCOME_RESPONSE_PATTERN = new SjpMessagePattern(SjpMessageType.RESPONSE, "welcome", "wandering-monster");
	private final Map<SocketAddress, SjpDiscoveryReceiver> receivers = new ConcurrentHashMap<>();
	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(16);
	private final DatagramSocket socket;

	public SjpDiscovery(SocketAddress address) throws SocketException {
		socket = new DatagramSocket(address);
	}

	public SjpDiscovery(InetAddress address, int port) throws SocketException {
		this(new InetSocketAddress(address, port));
	}

	public SjpDiscovery(int port) throws SocketException {
		this(new InetSocketAddress(port));
	}

	public SjpDiscovery() throws SocketException {
		socket = new DatagramSocket();
	}

	public void close() {
		socket.close();
		executorService.shutdown();
	}

	public void start() {
		receive((address, message) -> {
			if (WELCOME_REQUEST_PATTERN.match(message) && message.getId() != null) {
				try {
					socket.send(WELCOME_RESPONSE_PATTERN
							.createMessage((long) message.getId())
							.toBuffer()
							.toDatagramPacket(address));
				} catch (IOException ignored) {
				}
			}
		});
	}

	public void discover(Consumer<SocketAddress> consumer, SocketAddress broadcastAddress, int poll, long interval) {
		Random random = new Random();
		Queue<Long> requestIds = new ConcurrentLinkedQueue<>();

		receive((address, message) -> {
			if (WELCOME_RESPONSE_PATTERN.match(message) && requestIds.contains(message.getId())) {
				consumer.accept(address);
			}
		});

		executorService.scheduleAtFixedRate(() -> {
			try {
				long id = random.nextLong();
				socket.send(WELCOME_REQUEST_PATTERN.createMessage(id).toBuffer().toDatagramPacket(broadcastAddress));
				requestIds.add(id);
				if (requestIds.size() > poll) {
					requestIds.poll();
				}
			} catch (IOException ignored) {
			}
		}, 0, interval, TimeUnit.MILLISECONDS);
	}

	private void receive(BiConsumer<SocketAddress, SjpMessage> consumer) {
		executorService.submit(() -> {
			byte[] buffer = new byte[1024];
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
								ChronoUnit.MILLIS) > 5000L)
				.map(Map.Entry::getKey)
				.forEach(receivers::remove), 0, 1000L, TimeUnit.MILLISECONDS);
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
