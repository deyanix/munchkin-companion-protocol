package com.recadel.sjp.demo;

import com.recadel.sjp.SjpMessage;
import com.recadel.sjp.SjpSocket;

import java.io.IOException;
import java.net.Socket;

public class ClientMain {
	public static void main(String[] args) throws Exception {
		try  {
			Socket socket = new Socket("127.0.0.1", 1234);
			SjpSocket sjpSocket = new SjpSocket(socket);
			sjpSocket.setup();
			sjpSocket.send(SjpMessage.fromString("{\"test\": 1}"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
