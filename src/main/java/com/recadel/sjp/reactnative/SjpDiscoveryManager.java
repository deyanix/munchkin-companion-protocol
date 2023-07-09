package com.recadel.sjp.reactnative;

import com.recadel.sjp.discovery.SjpDiscoveryClient;
import com.recadel.sjp.discovery.SjpDiscoveryServer;
import com.recadel.sjp.exception.SjpException;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SjpDiscoveryManager {
	private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
	private Map<Integer, SjpSocketManager> sockets = new HashMap<>();

	public void createServer(int id, int port) {
		if (sockets.containsKey(id)) {
			throw new SjpException("Created already socket with given id");
		}

		try {
			SjpDiscoveryServer server = new SjpDiscoveryServer(executorService, port);
			SjpDiscoveryServerManager manager = new SjpDiscoveryServerManager(id, server);
			manager.start();
			sockets.put(id, manager);
		} catch (SocketException | UnknownHostException ex) {
			throw new SjpException("Occurs exception during creating discovery server", ex);
		}
	}

	public void createClient(int id, SocketAddress address) {
		if (sockets.containsKey(id)) {
			throw new SjpException("Created already socket with given id");
		}

		try {
			SjpDiscoveryClient client = new SjpDiscoveryClient(executorService);
			SjpDiscoveryClientManager manager = new SjpDiscoveryClientManager(id, client);
			manager.start(address);
		} catch (SocketException ex) {
			throw new SjpException("Occurs exception during creating discovery client", ex);
		}
	}

	public void close(int id) {
		if (!sockets.containsKey(id)) {
			throw new SjpException("Socket with given id not exists");
		}

		try {
			sockets.get(id).close();
		} catch (IOException ex) {
			throw new SjpException("Occurs exception during closing socket", ex);
		}
	}
}
