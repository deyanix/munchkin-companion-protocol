package com.munchkincompanion;

import com.munchkincompanion.game.controller.GuestGameController;
import com.munchkincompanion.game.entity.PlayerData;
import com.munchkincompanion.game.entity.PlayerGender;
import com.recadel.sjp.common.SjpReceiverGarbageCollector;
import com.recadel.sjp.messenger.SjpMessenger;
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
		GuestGameController gameController = new GuestGameController(messenger);
		gameController.addUpdateListener(System.out::println);
		gameController.synchronizePlayers();

		PlayerData data1 = new PlayerData();
		data1.setName("Michał Janiak");
		data1.setLevel(5);
		data1.setGear(6);
		data1.setGender(PlayerGender.MALE);
		data1.setGenderChanged(false);
		gameController.createPlayer(data1);
	}
}
