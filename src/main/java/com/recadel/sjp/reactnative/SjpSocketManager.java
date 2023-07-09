package com.recadel.sjp.reactnative;

import java.io.Closeable;
import java.io.IOException;

public abstract class SjpSocketManager implements Closeable {
	private int id;

	public SjpSocketManager(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public abstract void close() throws IOException;
}
