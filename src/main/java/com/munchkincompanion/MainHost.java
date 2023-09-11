package com.munchkincompanion;

import com.munchkincompanion.game.controller.HostGameController;
import com.munchkincompanion.game.entity.PlayerData;
import com.munchkincompanion.game.entity.PlayerGender;
import com.recadel.sjp.common.SjpReceiverGarbageCollector;
import com.recadel.sjp.messenger.SjpMessenger;
import com.recadel.sjp.messenger.SjpServerMediator;
import com.recadel.sjp.messenger.SjpServerMessengerListener;

import java.net.ServerSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MainHost {
	private final static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(8);
	private final static SjpReceiverGarbageCollector garbageCollector = new SjpReceiverGarbageCollector(executorService);
	public static void main(String[] args) throws Exception {
		ServerSocket socket = new ServerSocket(1234);
		SjpServerMediator mediator = new SjpServerMediator(socket);
		mediator.setGarbageCollector(garbageCollector);
		mediator.start(executorService);
		HostGameController gameController = new HostGameController(mediator);
		gameController.addUpdateListener(System.out::println);

		PlayerData data1 = new PlayerData();
		data1.setName("Krzysztof Fryta");
		data1.setLevel(4);
		data1.setGear(2);
		data1.setGender(PlayerGender.FEMALE);
		data1.setGenderChanged(true);
		gameController.createPlayer(data1);

		PlayerData data2 = new PlayerData();
		data2.setName("Andrzej Chmiel");
		data2.setLevel(5);
		data2.setGear(1);
		data2.setGender(PlayerGender.MALE);
		data2.setGenderChanged(false);
		gameController.createPlayer(data2);

	}
}
