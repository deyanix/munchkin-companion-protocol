package com.recadel.sjp.discovery;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class SjpDiscoveryServer extends SjpDiscoveryConnection {
	public SjpDiscoveryServer(InetAddress address, int port) throws SocketException {
		super(new InetSocketAddress(address, port));
	}

	public SjpDiscoveryServer(int port) throws SocketException {
		super(new InetSocketAddress(port));
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
