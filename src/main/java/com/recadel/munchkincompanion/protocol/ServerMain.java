package com.recadel.munchkincompanion.protocol;

import com.recadel.sjp.connection.SjpServer;

public class ServerMain {
	public static void main(String[] args) throws Exception {
		SjpServer server = new SjpServer(1234);
		server.start();
		System.out.println("Server started.");
	}
}
