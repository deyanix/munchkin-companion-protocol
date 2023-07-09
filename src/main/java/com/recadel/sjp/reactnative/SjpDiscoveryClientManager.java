package com.recadel.sjp.reactnative;

import com.recadel.sjp.discovery.SjpDiscoveryClient;

import java.io.IOException;
import java.net.SocketAddress;

public class SjpDiscoveryClientManager extends SjpSocketManager {
	private final SjpDiscoveryClient client;

	public SjpDiscoveryClientManager(int id, SjpDiscoveryClient client) {
		super(id);
		this.client = client;
	}

	public void start(SocketAddress broadcastAddress) {
		client.discover((address) -> {
			System.out.println("[CLIENT MANAGER] Found " + address);
			client.close();
		}, broadcastAddress);
	}

	@Override
	public void close() throws IOException {
		client.close();
	}
}
