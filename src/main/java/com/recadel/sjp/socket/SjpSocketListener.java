package com.recadel.sjp.socket;

import com.recadel.sjp.common.SjpMessageBuffer;

public interface SjpSocketListener {
	void onMessage(SjpMessageBuffer message);
	void onError();
	void onClose();
}
