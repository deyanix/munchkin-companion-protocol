package com.munchkincompanion;

import com.recadel.sjp.common.SjpReceiverGarbageCollector;
import com.recadel.sjp.messenger.SjpMessenger;
import com.recadel.sjp.messenger.SjpMessengerReceiver;
import com.recadel.sjp.messenger.SjpServerMessenger;
import com.recadel.sjp.messenger.SjpServerMessengerListener;

import java.net.ServerSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MainHost {
	private final static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(8);
	private final static SjpReceiverGarbageCollector garbageCollector = new SjpReceiverGarbageCollector(executorService);
	public static void main(String[] args) throws Exception {
		ServerSocket socket = new ServerSocket(1234);

		SjpServerMessenger messenger = new SjpServerMessenger(socket);
		messenger.start(executorService);
		messenger.addListener(new SjpServerMessengerListener() {
			@Override
			public void onConnect(SjpMessenger messenger) {
				System.out.println("[HOST] Connect");
				messenger.emit("cool", "data");
				messenger.addReceiver(new SjpMessengerReceiver() {
					@Override
					public void onEvent(String action, Object data) {
						System.out.println("[HOST] Event " + action + " : " + data);
					}

					@Override
					public void onRequest(String action, Object data) {
						System.out.println("[HOST] Request " + action + " : " + data);
					}
				});
			}

			@Override
			public void onClose() {
				System.out.println("[HOST] Close");
			}

			@Override
			public void onError(Throwable throwable) {
				System.out.println("[HOST] Error");
			}
		});
	}
}
