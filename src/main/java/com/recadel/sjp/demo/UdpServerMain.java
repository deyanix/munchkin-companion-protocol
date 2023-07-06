package com.recadel.sjp.demo;

import com.recadel.sjp.connection.SjpMessage;
import com.recadel.sjp.connection.SjpMessageBuffer;
import com.recadel.sjp.connection.SjpMessageType;

import java.io.IOException;
import java.net.*;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;

public class UdpServerMain {
	private static final Map<SocketAddress, BlockingQueue<SjpMessageBuffer>> buffers = new ConcurrentHashMap<>();
	private static final ExecutorService executorService = Executors.newFixedThreadPool(16);
	private static DatagramSocket socket;

	public static void replyForWelcome(SocketAddress address, long id) throws IOException {
		SjpMessage message = SjpMessage.createResponse(id, "wandering-monster");
		byte[] buffer = message.toBuffer().getBuffer();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address);
		socket.send(packet);
	}

	public static void createThread(SocketAddress address, BlockingQueue<SjpMessageBuffer> queue) {
		buffers.put(address, queue);
		System.out.printf("[%s] Created", address.toString());
		System.out.println();

		CompletableFuture<Optional<SjpMessageBuffer>> future = CompletableFuture.supplyAsync(() -> {
			SjpMessageBuffer allMessage = new SjpMessageBuffer();
			while (!executorService.isShutdown()) {
				try {
					SjpMessageBuffer buffer = queue.poll(1, TimeUnit.SECONDS);
					if (buffer == null) {
						System.out.printf("[%s] Timeout", address);
						System.out.println();
						return Optional.empty();
					}
					System.out.printf("[%s] Received: %s", address, buffer);
					System.out.println();

					allMessage = allMessage.append(buffer);
					if (allMessage.isCompleted()) {
						System.out.printf("[%s] Completed", address.toString());
						System.out.println();
						return Optional.of(allMessage);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					return Optional.empty();
				}
			}
			return Optional.empty();
		}, executorService);
		future.thenAccept((optional) -> {
			optional.ifPresent(buffer -> {
				System.out.printf("[%s] Finished: %s", address, buffer.getText());
				System.out.println();
				SjpMessage message = SjpMessage.fromBuffer(buffer);
				if (message.getType() == SjpMessageType.REQUEST &&
						message.getAction().equals("welcome") &&
						Objects.equals(message.getData(), "look-for-trouble")) {
					try {
						replyForWelcome(address, message.getId());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			buffers.remove(address);
		}).exceptionally((err) -> {

			System.out.printf("[%s] Error: %s", address, err);
			System.out.println();
			return null;
		});
	}

	public static void handlePacket(DatagramPacket packet) {
		SocketAddress address = packet.getSocketAddress();
		SjpMessageBuffer buffer = SjpMessageBuffer.fromDatagramPacket(packet);

		if (buffers.containsKey(address)) {
			buffers.get(address).add(buffer);
		} else {
			BlockingQueue<SjpMessageBuffer> queue = new LinkedBlockingQueue<>();
			queue.add(buffer);
			createThread(address, queue);
		}
	}

	public static void main(String[] args) throws Exception {
		socket = new DatagramSocket(12345);

		byte[] buffer = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		while (true) {
			socket.receive(packet);
			handlePacket(packet);
		}


//		socket.close();
	}
}
