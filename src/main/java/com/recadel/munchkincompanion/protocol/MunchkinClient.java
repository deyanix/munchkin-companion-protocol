package com.recadel.munchkincompanion.protocol;

import com.recadel.sjp.connection.SjpSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MunchkinClient {
	private final SjpSocket socket;
	private final MunchkinConnection connection;

	public MunchkinClient(InetAddress address, int port) throws IOException {
		Socket tcpSocket = new Socket();
		tcpSocket.connect(new InetSocketAddress(address, port));
		socket = new SjpSocket(tcpSocket);
		connection = new MunchkinConnection(socket);
		socket.setup();
	}

	public MunchkinClient(String host, int port) throws IOException {
		this(InetAddress.getByName(host), port);
	}

	public SjpSocket getSocket() {
		return socket;
	}

	public MunchkinConnection getConnection() {
		return connection;
	}
}
