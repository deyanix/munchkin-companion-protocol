package com.recadel.sjp.demo;

import com.recadel.munchkincompanion.protocol.MunchkinClient;
import com.recadel.sjp.connection.SjpMessage;
import com.recadel.sjp.connection.SjpSocket;

import java.net.Socket;

public class ClientMain {
	public static void main(String[] args) throws Exception {
		MunchkinClient client = new MunchkinClient("127.0.0.1", 1234);
		System.out.println("Client started.");
		Object res = client.getConnection().requestAsync("join", "Test join").get();
		System.out.println("Response from server: " + res);
	}
}
