package com.recadel.sjp.common;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class SjpReceiver {
	private SjpMessageBuffer currentMessage = new SjpMessageBuffer();
	private LocalDateTime lastReceivedBuffer = LocalDateTime.now();

	public Queue<SjpMessageBuffer> receiveAll(SjpMessageBuffer buffer) {
		Queue<SjpMessageBuffer> messages = new LinkedList<>();

		int offset = 0;
		while (offset < buffer.getLength() - 1) {
			int endIndex = buffer.indexOf(SjpMessageBuffer.END_OF_MESSAGE, offset);
			if (endIndex < 0) {
				endIndex = buffer.getLength() - 1;
			} else {
				endIndex += SjpMessageBuffer.END_OF_MESSAGE.getLength() - 1;
			}

			receive(buffer.slice(offset, endIndex)).ifPresent(messages::add);
			offset = endIndex;
		}
		return messages;
	}

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


	public void clear() {
		currentMessage = new SjpMessageBuffer();
	}

	public LocalDateTime getLastReceivedBuffer() {
		return lastReceivedBuffer;
	}
}
