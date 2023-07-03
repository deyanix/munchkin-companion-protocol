package com.recadel.sjp;

public interface SjpSocketListener {
	void onMessage(SjpMessage message);
	void onError();
	void onClose();
}
