package com.recadel.sjp.reactnative;

import com.recadel.sjp.discovery.SjpDiscoveryServer;

import java.io.IOException;

public class SjpDiscoveryServerManager extends SjpSocketManager {
	private final SjpDiscoveryServer server;

	public SjpDiscoveryServerManager(int id, SjpDiscoveryServer server) {
		super(id);
		this.server = server;
	}

	public void start() {
		server.start();
	}

	@Override
	public void close() throws IOException {
		server.close();
	}
}
