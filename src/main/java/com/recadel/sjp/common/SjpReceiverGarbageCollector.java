package com.recadel.sjp.common;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SjpReceiverGarbageCollector {
	private final List<SjpReceiver> receivers = new CopyOnWriteArrayList<>();
	private final ScheduledExecutorService executorService;
	private long receiverLifetime = 5000L;
	private ScheduledFuture<?> future;

	public SjpReceiverGarbageCollector(ScheduledExecutorService executorService) {
		this.executorService = executorService;
	}

	public SjpReceiverGarbageCollector() {
		this(Executors.newSingleThreadScheduledExecutor());
	}

	public long getReceiverLifetime() {
		return receiverLifetime;
	}

	public void setReceiverLifetime(long receiverLifetime) {
		this.receiverLifetime = receiverLifetime;
	}

	public void registerReceiver(SjpReceiver receiver) {
		receivers.add(receiver);
	}

	public void unregisterReceiver(SjpReceiver receiver) {
		receivers.remove(receiver);
	}

	public void start() {
		if (future == null || !future.isCancelled()) {
			return;
		}

		future = executorService.scheduleAtFixedRate(() ->
			receivers.parallelStream()
					.filter(receiver ->
							receiver.getLastReceivedBuffer().until(
									LocalDateTime.now(),
									ChronoUnit.MILLIS) > receiverLifetime)
					.forEach(SjpReceiver::clear),
				0, receiverLifetime, TimeUnit.MILLISECONDS);
	}

	public void stop() {
		future.cancel(false);
		future = null;
	}
}
