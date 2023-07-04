package com.recadel.sjp.demo;

import com.recadel.munchkincompanion.protocol.MunchkinServer;

public class ServerMain {
	public static void main(String[] args) throws Exception {
		MunchkinServer server = new MunchkinServer(1234);
		server.start();
		System.out.println("Server started.");
	}
}
