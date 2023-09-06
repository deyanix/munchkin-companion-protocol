package com.recadel.sjp.discovery;

import org.json.JSONException;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
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
	private final SocketAddress broadcastAddress;
	private int identifiersPool = 10;
	private long interval = 5000L;
	private ScheduledFuture<?> senderFuture;

	public SjpDiscoveryClient(SocketAddress broadcastAddress) throws SocketException {
		super(new DatagramSocket());
		this.broadcastAddress = broadcastAddress;
	}

	public void discover(Consumer<InetSocketAddress> consumer, ScheduledExecutorService executorService) {
		final int localIdentifiersPool = identifiersPool;
		Random random = new Random();
		Queue<Long> requestIds = new ConcurrentLinkedQueue<>();

		receive((address, message) -> {
			if (address instanceof InetSocketAddress &&
					WELCOME_RESPONSE_PATTERN.match(message) &&
					requestIds.contains(message.getId())) {
				consumer.accept((InetSocketAddress) address);
			}
		}, executorService);

		senderFuture = executorService.scheduleAtFixedRate(() -> {
			try {
				long id = random.nextLong();
				socket.send(WELCOME_REQUEST_PATTERN.createMessage(id).toBuffer().toDatagramPacket(broadcastAddress));
				requestIds.add(id);
				if (requestIds.size() > localIdentifiersPool) {
					requestIds.poll();
				}
			} catch (IOException | JSONException ignored) {
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
