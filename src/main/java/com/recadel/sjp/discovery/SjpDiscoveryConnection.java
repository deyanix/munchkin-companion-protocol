package com.recadel.sjp.discovery;

import com.recadel.sjp.connection.SjpMessageBuffer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SjpDiscoveryConnection {
	private static final long DEFAULT_TIMEOUT = 1000L;
	private final BlockingQueue<SjpMessageBuffer> queue = new LinkedBlockingQueue<>();
	private final DatagramSocket socket;
	private final SocketAddress address;
	private final ExecutorService executorService;

	public SjpDiscoveryConnection(DatagramSocket socket, SocketAddress address, ExecutorService executorService) {
		this.socket = socket;
		this.address = address;
		this.executorService = executorService;
	}

	public boolean handlePacket(DatagramPacket packet) {
		if (address.equals(packet.getSocketAddress())) {
			queue.add(SjpMessageBuffer.fromDatagramPacket(packet));
			return true;
		}
		return false;
	}

	public CompletableFuture<Optional<SjpMessageBuffer>> receive() {
		return receive(DEFAULT_TIMEOUT);
	}

	public CompletableFuture<Optional<SjpMessageBuffer>> receive(long timeout) {
		return CompletableFuture
				.supplyAsync(() -> {
					SjpMessageBuffer allMessage = new SjpMessageBuffer();
					while (!executorService.isShutdown()) {
						try {
							SjpMessageBuffer buffer = queue.poll(timeout, TimeUnit.MILLISECONDS);
							if (buffer == null) {
								break;
							}

							allMessage = allMessage.append(buffer);
							if (allMessage.isValid()) {
								return Optional.of(allMessage);
							}
						} catch (InterruptedException e) {
							break;
						}
					}
					return Optional.empty();
				}, executorService);
	}

	public void send(SjpMessageBuffer buffer) throws IOException {
	}
}
