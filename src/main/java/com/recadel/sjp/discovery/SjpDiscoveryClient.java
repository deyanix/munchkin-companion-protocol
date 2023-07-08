package com.recadel.sjp.discovery;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class SjpDiscoveryClient extends SjpDiscoveryConnection {
	public SjpDiscoveryClient(ScheduledExecutorService executorService) throws SocketException {
		super(executorService);
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
}
