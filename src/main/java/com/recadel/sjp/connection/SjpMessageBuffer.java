package com.recadel.sjp.connection;

import org.json.JSONException;
import org.json.JSONTokener;

import java.net.DatagramPacket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SjpMessageBuffer {
	public static final SjpMessageBuffer END_OF_MESSAGE = new SjpMessageBuffer(new byte[] { '\r', '\n' });
	public static final Charset CHARSET = StandardCharsets.UTF_8;

	public static SjpMessageBuffer fromString(String text) {
		return new SjpMessageBuffer(text.getBytes(CHARSET)).append(END_OF_MESSAGE);
	}

	public static SjpMessageBuffer fromDatagramPacket(DatagramPacket packet) {
		return new SjpMessageBuffer(packet.getData()).slice(0, packet.getLength() - 1);
	}

	public static SjpMessageBuffer concat(SjpMessageBuffer msg1, SjpMessageBuffer msg2) {
		byte[] dest = new byte[msg1.buffer.length + msg2.buffer.length];
		System.arraycopy(msg1.buffer, 0, dest, 0, msg1.buffer.length);
		System.arraycopy(msg2.buffer, 0, dest, msg1.buffer.length, msg2.buffer.length);
		return new SjpMessageBuffer(dest);
	}

	private final byte[] buffer;

	public SjpMessageBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	public SjpMessageBuffer() {
		this(new byte[0]);
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public int getLength() {
		return buffer.length;
	}

	public boolean isEmpty() {
		return buffer.length == 0;
	}

	public SjpMessageBuffer prepend(SjpMessageBuffer buffer) {
		return concat(buffer, this);
	}

	public SjpMessageBuffer append(SjpMessageBuffer buffer) {
		return concat(this, buffer);
	}

	public int pos(int index) {
		if (index < 0) {
			index += getLength();
		}

		if (index < 0 || index >= getLength()) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return index;
	}

	public SjpMessageBuffer slice(int start, int end) {
		start = pos(start);
		end = pos(end);
		byte[] dest = new byte[end - start + 1];
		System.arraycopy(buffer, start, dest, 0, dest.length);
		return new SjpMessageBuffer(dest);
	}

	public SjpMessageBuffer slice(int start) {
		return slice(start, buffer.length - 1);
	}

	public boolean isCompleted() {
		return buffer.length >= END_OF_MESSAGE.getLength() &&
				Arrays.equals(slice(-END_OF_MESSAGE.getLength()).buffer, END_OF_MESSAGE.buffer);
	}

	public int indexOf(SjpMessageBuffer message, int offset) {
		int found = 0;
		for (int i = offset; i < buffer.length; i++) {
			if (buffer[i] == message.buffer[found]) {
				found++;
				if (found == message.buffer.length) {
					return i - message.buffer.length + 1;
				}
			} else {
				found = 0;
			}
		}
		return -1;
	}

	public int indexOf(SjpMessageBuffer message) {
		return indexOf(message, 0);
	}

	public boolean isValid() {
		if (!isCompleted()) {
			return false;
		}

		JSONTokener tokener = new JSONTokener(getText());
		try {
			tokener.nextValue();
			while (tokener.more()) {
				if (!Character.isWhitespace(tokener.next())) {
					return false;
				}
			}
			return !tokener.more();
		} catch (JSONException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public String getText() {
		 return slice(0, -END_OF_MESSAGE.getLength()).toString();
	}

	@Override
	public String toString() {
		return new String(buffer, CHARSET);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SjpMessageBuffer that = (SjpMessageBuffer) o;
		return Arrays.equals(buffer, that.buffer);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(buffer);
	}
}
