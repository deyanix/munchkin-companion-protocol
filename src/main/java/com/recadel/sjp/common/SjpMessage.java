package com.recadel.sjp.common;

import org.json.JSONObject;
import org.json.JSONTokener;

public class SjpMessage {
	public static SjpMessage fromBuffer(SjpMessageBuffer buffer) {
		JSONTokener tokener = new JSONTokener(buffer.getText());
		if (!tokener.more()) {
			throw new IllegalArgumentException("Bad message buffer format: Invalid JSON");
		}

		Object object = tokener.nextValue();
		if (!(object instanceof JSONObject)) {
			throw new IllegalArgumentException("Bad message buffer format: Excepted JSONObject");
		}
		JSONObject json = (JSONObject) object;

		Long id = null;
		String action = null;
		SjpMessageType type;
		Object data = null;

		if (json.has("type")) {
			String typeString = json.getString("type");
			type = SjpMessageType.valueOf(typeString.toUpperCase());
		} else {
			throw new IllegalArgumentException("Bad message format: Unknown message type");
		}

		if (json.has("id")) {
			id = json.getLong("id");
		}
		if (json.has("action")) {
			action = json.getString("action");
		}
		if (json.has("data")) {
			data = json.get("data");
		}
		return new SjpMessage(type, action, id, data);
	}

	public static SjpMessage createRequest(String action, long id, Object data) {
		return new SjpMessage(SjpMessageType.REQUEST, action, id, data);
	}

	public static SjpMessage createResponse(long id, Object data) {
		return new SjpMessage(SjpMessageType.RESPONSE, null, id, data);
	}

	public static SjpMessage createEvent(String action, Object data) {
		return new SjpMessage(SjpMessageType.EVENT, action, null, data);
	}

	private final SjpMessageType type;
	private final String action;
	private final Long id;
	private final Object data;

	public SjpMessage(SjpMessageType type, String action, Long id, Object data) {
		this.type = type;
		this.action = action;
		this.id = id;
		this.data = data;
	}

	public SjpMessageType getType() {
		return type;
	}

	public String getAction() {
		return action;
	}

	public Long getId() {
		return id;
	}

	public Object getData() {
		return data;
	}

	public JSONObject toJSON() {
		return new JSONObject()
			.put("type", type.toString().toLowerCase())
			.put("action", action)
			.put("id", id)
			.put("data", data);
	}

	public SjpMessageBuffer toBuffer() {
		return SjpMessageBuffer.fromString(toJSON().toString());
	}
}
