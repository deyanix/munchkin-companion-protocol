package com.recadel.sjp.demo;

import com.recadel.sjp.connection.SjpMessage;
import com.recadel.sjp.connection.SjpMessageBuffer;
import com.recadel.sjp.connection.SjpSocket;
import com.recadel.sjp.connection.SjpSocketListener;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerMain {
	private final List<SjpSocket> sockets = new ArrayList<>();
	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = new ServerSocket(1234);
		while (!serverSocket.isClosed()) {
			try {
				Socket socket = serverSocket.accept();
				SjpSocket sjpSocket = new SjpSocket(socket);
				sjpSocket.addListener(new SjpSocketListener() {
					@Override
					public void onMessage(SjpMessageBuffer message) {
						System.out.println("Message " + SjpMessage.fromBuffer(message));
					}

					@Override
					public void onError() {
						System.out.println("Error");
					}

					@Override
					public void onClose() {
						System.out.println("Close");
					}
				});

				sjpSocket.setup();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
