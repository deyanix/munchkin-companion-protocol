package com.recadel.sjp.demo;

import com.recadel.sjp.SjpMessage;
import com.recadel.sjp.SjpSocket;
import com.recadel.sjp.SjpSocketListener;

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
					public void onMessage(SjpMessage message) {
						System.out.println("Message " + message.getText());
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
