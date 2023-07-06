package com.recadel.sjp.discovery;

import com.recadel.sjp.connection.SjpMessageBuffer;

import java.time.LocalDateTime;
import java.util.Optional;

public class SjpDiscoveryReceiver {
	private SjpMessageBuffer currentMessage = new SjpMessageBuffer();
	private LocalDateTime lastReceivedBuffer = LocalDateTime.now();

	public Optional<SjpMessageBuffer> receive(SjpMessageBuffer buffer) {
		lastReceivedBuffer = LocalDateTime.now();
		currentMessage = currentMessage.append(buffer);
		if (currentMessage.isValid()) {
			SjpMessageBuffer message = currentMessage;
			currentMessage = new SjpMessageBuffer();
			return Optional.of(message);
		}
		return Optional.empty();
	}

	public LocalDateTime getLastReceivedBuffer() {
		return lastReceivedBuffer;
	}
}
