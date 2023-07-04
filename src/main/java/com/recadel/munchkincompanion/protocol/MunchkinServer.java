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

public class MunchkinServer {
	private final ServerSocket serverSocket;
	private final Map<Long, MunchkinConnection> connections = new ConcurrentHashMap<>();
	private long nextConnectionId = 1;
	private boolean running = true;

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
		running = false;
		serverSocket.close();
	}

	public void start() throws IOException {
		new Thread(() -> {
			while (running) {
				try {
					Socket socket = serverSocket.accept();
					SjpSocket sjpSocket = new SjpSocket(socket);
					sjpSocket.setup();
					long id = nextConnectionId++;
					MunchkinConnection connection = new MunchkinConnection(id, sjpSocket);
					connection.emit("welcome", 1);
					connections.put(id, connection);
				} catch (IOException | SjpException ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}
}
