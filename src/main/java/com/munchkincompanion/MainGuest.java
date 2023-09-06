package com.munchkincompanion;

import com.recadel.sjp.common.SjpReceiverGarbageCollector;
import com.recadel.sjp.messenger.SjpMessenger;
import com.recadel.sjp.messenger.SjpMessengerReceiver;
import com.recadel.sjp.socket.SjpSocket;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MainGuest {
	private final static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(8);
	private final static SjpReceiverGarbageCollector garbageCollector = new SjpReceiverGarbageCollector(executorService);
	public static void main(String[] args) throws Exception {
		Socket tcpSocket = new Socket();
		tcpSocket.connect(new InetSocketAddress("127.0.0.1", 1234));

		SjpSocket socket = new SjpSocket(tcpSocket);
		socket.applyGarbageCollector(garbageCollector);
		socket.setup(executorService);

		SjpMessenger messenger = new SjpMessenger(socket);
		messenger.addReceiver(new SjpMessengerReceiver() {
			@Override
			public void onEvent(String action, Object data) {
				System.out.println("[GUEST] Event " + action + " : " + data);
			}

			@Override
			public void onRequest(String action, Object data) {
				System.out.println("[GUEST] Event " + action + " : " + data);
			}
		});

		messenger.emit("test", "Test");
	}
}
