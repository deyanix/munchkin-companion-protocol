package com.recadel.munchkincompanion.protocol;

import com.recadel.sjp.connection.SjpSocket;
import com.recadel.sjp.exception.SjpException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MunchkinServer {
	private final ServerSocket serverSocket;
	private final Map<Long, MunchkinConnection> connections = new ConcurrentHashMap<>();
	private final ExecutorService executorService = Executors.newFixedThreadPool(64);
	private long nextConnectionId = 1L;

	public MunchkinServer(InetAddress address, int port) throws IOException {
		serverSocket = new ServerSocket();
		serverSocket.bind(new InetSocketAddress(address, port));
	}

	public MunchkinServer(String host, int port) throws IOException {
		this(InetAddress.getByName(host), port);
	}

	public MunchkinServer(int port) throws IOException {
		this("0.0.0.0", port);
	}

	public void close() throws IOException {
		executorService.shutdown();
		serverSocket.close();
	}

	public void start() throws IOException {
		executorService.submit(() -> {
			while (!executorService.isShutdown()) {
				try {
					Socket socket = serverSocket.accept();
					SjpSocket sjpSocket = new SjpSocket(socket, executorService);
					sjpSocket.setup();
					long id = nextConnectionId++;
					MunchkinConnection connection = new MunchkinConnection(id, sjpSocket);
					connection.emit("welcome", 1);
					connections.put(id, connection);
				} catch (IOException | SjpException ex) {
					ex.printStackTrace();
				}
			}
		});
	}
}
