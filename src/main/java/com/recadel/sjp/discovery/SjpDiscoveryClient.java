package com.recadel.sjp.discovery;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class SjpDiscoveryClient extends SjpDiscoveryConnection {
	private int identifiersPool = 10;
	private long interval = 5000L;
	private ScheduledFuture<?> senderFuture;

	public SjpDiscoveryClient(ScheduledExecutorService executorService) throws SocketException {
		super(executorService, new DatagramSocket());
	}

	public void discover(Consumer<SocketAddress> consumer, SocketAddress broadcastAddress) {
		final int localIdentifiersPool = identifiersPool;
		Random random = new Random();
		Queue<Long> requestIds = new ConcurrentLinkedQueue<>();

		System.out.println("[CLIENT] Started");
		receive((address, message) -> {
			if (WELCOME_RESPONSE_PATTERN.match(message) && requestIds.contains(message.getId())) {
				consumer.accept(address);
			}
		});

		senderFuture = executorService.scheduleAtFixedRate(() -> {
			try {
				System.out.println("[CLIENT] Sent message");
				long id = random.nextLong();
				socket.send(WELCOME_REQUEST_PATTERN.createMessage(id).toBuffer().toDatagramPacket(broadcastAddress));
				requestIds.add(id);
				if (requestIds.size() > localIdentifiersPool) {
					requestIds.poll();
				}
			} catch (IOException ignored) {
			}
		}, 0, interval, TimeUnit.MILLISECONDS);
	}

	public void close() {
		super.close();
		senderFuture.cancel(false);
	}

	public int getIdentifiersPool() {
		return identifiersPool;
	}

	public void setIdentifiersPool(int identifiersPool) {
		this.identifiersPool = identifiersPool;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}
}
