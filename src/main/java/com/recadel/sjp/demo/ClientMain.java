package com.recadel.sjp.demo;

import com.recadel.munchkincompanion.protocol.MunchkinClient;

public class ClientMain {
	public static void main(String[] args) throws Exception {
		MunchkinClient client = new MunchkinClient("127.0.0.1", 1234);
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
