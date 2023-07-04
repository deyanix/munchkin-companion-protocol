package com.recadel.sjp.connection;

public interface SjpSocketListener {
	void onMessage(SjpMessageBuffer message);
	void onError();
	void onClose();
}
