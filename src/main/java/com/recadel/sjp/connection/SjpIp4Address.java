package com.recadel.sjp.connection;

import java.net.Inet4Address;
import java.net.InetAddress;

public class SjpIp4Address {
	private final long address;

	public SjpIp4Address(long address) {
		this.address = address;
	}

	public SjpIp4Address(int a1, int a2, int a3, int a4) {
		this(
				((a1 & 0xFFL) << 24) |
				((a2 & 0xFFL) << 16) |
				((a3 & 0xFFL) <<  8) |
				((a4 & 0xFFL))
		);
	}

	public SjpIp4Address(byte[] address) {
		this(address[0], address[1], address[2], address[3]);
	}

	public SjpIp4Address(Inet4Address address) {
		this(address.getAddress());
	}

	public long getAddress() {
		return address;
	}

	public byte[] getBytes() {
		return new byte[] {
				(byte) ((address >> 24) & (0xFF)),
				(byte) ((address >> 16) & (0xFF)),
				(byte) ((address >>  8) & (0xFF)),
				(byte) ((address      ) & (0xFF))
		};
	}

	public SjpIp4Address add(long offset) {
		return new SjpIp4Address(address + offset);
	}
}
