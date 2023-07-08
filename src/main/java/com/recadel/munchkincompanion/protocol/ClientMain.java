package com.recadel.munchkincompanion.protocol;

import com.recadel.sjp.connection.SjpClient;

public class ClientMain {
	public static void main(String[] args) throws Exception {
		SjpClient client = new SjpClient("127.0.0.1", 1234);
		System.out.println("Client started.");

		client.getConnection()
				.requestAndWait("join", "Test join")
				.thenAccept(System.out::println)
				.exceptionally(throwable -> {
					throwable.printStackTrace();
					return null;
				});
		client.getConnection()
				.requestAndWait("welcome", "Test join")
				.thenAccept(System.out::println)
				.exceptionally(throwable -> {
					throwable.printStackTrace();
					return null;
				});
	}
}
