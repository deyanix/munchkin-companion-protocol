package com.recadel.sjp.discovery;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.ScheduledExecutorService;

public class SjpDiscoveryServer extends SjpDiscoveryConnection {
	public SjpDiscoveryServer(ScheduledExecutorService executorService, InetAddress address, int port) throws SocketException {
		super(executorService, new InetSocketAddress(address, port));
	}

	public SjpDiscoveryServer(ScheduledExecutorService executorService, int port) throws SocketException {
		super(executorService, new InetSocketAddress(port));
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
}
