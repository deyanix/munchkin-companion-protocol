package com.recadel.sjp.connection;

import com.recadel.sjp.common.SjpMessageBuffer;

public class SjpAbstractSocketListener implements SjpSocketListener {
	@Override
	public void onMessage(SjpMessageBuffer message) {
	}

	@Override
	public void onError() {
	}

	@Override
	public void onClose() {
	}
}
