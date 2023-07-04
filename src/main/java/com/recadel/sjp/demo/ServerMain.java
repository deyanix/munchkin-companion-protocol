package com.recadel.sjp.demo;

import com.recadel.munchkincompanion.protocol.MunchkinServer;
import com.recadel.sjp.connection.SjpMessage;
import com.recadel.sjp.connection.SjpMessageBuffer;
import com.recadel.sjp.connection.SjpSocket;
import com.recadel.sjp.connection.SjpSocketListener;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerMain {
	public static void main(String[] args) throws Exception {
		MunchkinServer server = new MunchkinServer(1234);
		server.start();
		System.out.println("Server started.");
	}
}
