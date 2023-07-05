package com.recadel.sjp.demo;

import com.recadel.sjp.connection.SjpMessageBuffer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class UdpServerMain {
	private static boolean running = true;
	public static void main(String[] args) throws Exception {
		DatagramSocket socket = new DatagramSocket(12345);

		byte[] buffer = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		while (true) {
			SjpMessageBuffer messageBuffer = new SjpMessageBuffer();
			socket.receive(packet);
			messageBuffer = messageBuffer.append(new SjpMessageBuffer(buffer).slice(0, packet.getLength()));

			InetAddress address = packet.getAddress();
			int port = packet.getPort();
			System.out.printf("Connected to %s:%d", address.toString(), port);
			System.out.println();
			System.out.println(messageBuffer.getText());
		}


//		socket.close();
	}
}
