package com.recadel.sjp.discovery;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class SjpDiscoveryClient {
	private final DatagramSocket socket;
	private final SjpDiscoveryConnection connection;
	private final ExecutorService executorService = Executors.newCachedThreadPool();

	public SjpDiscoveryClient(SocketAddress address) throws SocketException {
		socket = new DatagramSocket(address);
		connection = new SjpDiscoveryConnection(socket, address, executorService);
	}

	public SjpDiscoveryClient(InetAddress address, int port) throws SocketException {
		this(new InetSocketAddress(address, port));
	}

	public SjpDiscoveryClient(int port) throws SocketException {
		this(new InetSocketAddress(port));
	}

}
