package com.recadel.sjp.common;

import java.util.Objects;

public class SjpMessagePattern {
	private final SjpMessageType type;
	private final String action;
	private final Object data;

	public SjpMessagePattern(SjpMessageType type, String action, Object data) {
		this.type = type;
		this.action = action;
		this.data = data;
	}

	public boolean shallowMatch(SjpMessage message) {
		return type == message.getType() &&	Objects.equals(action, message.getAction());
	}

	public boolean match(SjpMessage message) {
		return shallowMatch(message) &&	Objects.equals(data, message.getData());
	}

	public SjpMessage createMessage() {
		return new SjpMessage(type, action, null, data);
	}

	public SjpMessage createMessage(long id) {
		return new SjpMessage(type, action, id, data);
	}

	public SjpMessage createMessage(Object data) {
		return new SjpMessage(type, action, null, data);
	}

	public SjpMessage createMessage(long id, Object data) {
		return new SjpMessage(type, action, id, data);
	}
}
