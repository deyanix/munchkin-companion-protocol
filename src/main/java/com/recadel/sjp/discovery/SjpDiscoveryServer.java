package com.recadel.sjp.discovery;

import org.json.JSONException;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ScheduledExecutorService;

public class SjpDiscoveryServer extends SjpDiscoveryConnection {
	public SjpDiscoveryServer(SocketAddress address) throws SocketException {
		super(new DatagramSocket(address));
	}

	public SjpDiscoveryServer(int port) throws SocketException {
		super(new DatagramSocket(port));
	}

	public void start(ScheduledExecutorService executorService) {
		receive((address, message) -> {
			if (WELCOME_REQUEST_PATTERN.match(message) && message.getId() != null) {
				try {
					socket.send(WELCOME_RESPONSE_PATTERN
							.createMessage((long) message.getId())
							.toBuffer()
							.toDatagramPacket(address));
				} catch (IOException | JSONException ignored) {
				}
			}
		}, executorService);
	}
}
