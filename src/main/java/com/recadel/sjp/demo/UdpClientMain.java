package com.recadel.sjp.demo;

import com.recadel.sjp.connection.SjpMessage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class UdpClientMain {
	public static void main(String[] args) throws Exception {
		DatagramSocket socket = new DatagramSocket();
		InetAddress address = InetAddress.getByName("192.168.16.255");

		byte[] buffer = SjpMessage
				.createRequest("welcome", 0, "look-for-trouble")
				.toBuffer()
				.getBuffer();

		int chunkSize = 5;
		for(int i = 0; i < buffer.length; i += chunkSize){
			Thread.sleep(1000);
			byte[] chunk = Arrays.copyOfRange(buffer, i, Math.min(buffer.length, i + chunkSize));

			DatagramPacket packet = new DatagramPacket(chunk, chunk.length, address, 12345);
			socket.send(packet);
		}
		socket.close();

	}
}
