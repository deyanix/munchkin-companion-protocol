package com.recadel.sjp.discovery;

import com.recadel.sjp.connection.SjpMessage;
import com.recadel.sjp.connection.SjpMessageBuffer;

import java.util.Optional;

public class SjpDiscoveryReceiver {
	private SjpMessageBuffer currentMessage = new SjpMessageBuffer();

	public Optional<SjpMessageBuffer> receive(SjpMessageBuffer buffer) {
		currentMessage = currentMessage.append(buffer);
		if (currentMessage.isValid()) {
			SjpMessageBuffer message = currentMessage;
			currentMessage = new SjpMessageBuffer();
			return Optional.of(message);
		}
		return Optional.empty();
	}
}
