package com.recadel.sjp.connection;

import java.util.Iterator;

public class SjpIp4Network implements Iterable<SjpIp4Address> {
	private final SjpIp4Address address;
	private final SjpIp4Address subnetMask;

	public SjpIp4Network(SjpIp4Address address, SjpIp4Address subnetMask) {
		this.address = address;
		this.subnetMask = subnetMask;
	}

	public SjpIp4Address getAddress() {
		return address;
	}

	public SjpIp4Address getSubnetMask() {
		return subnetMask;
	}

	public SjpIp4Address getWildcardMask() {
		return new SjpIp4Address(~subnetMask.getAddress());
	}

	public SjpIp4Address getNetworkAddress() {
		return new SjpIp4Address(address.getAddress() & subnetMask.getAddress());
	}

	public SjpIp4NetworkIterator iterator() {
		return new SjpIp4NetworkIterator();
	}

	public class SjpIp4NetworkIterator implements Iterator<SjpIp4Address> {
		private SjpIp4Address current = getNetworkAddress().add(1);

		@Override
		public boolean hasNext() {
			return (~(current.getAddress() ^ ~getNetworkAddress().getAddress())
					& getSubnetMask().getAddress()) == 0;
		}

		@Override
		public SjpIp4Address next() {
			SjpIp4Address tmp = current;
			current = current.add(1);
			return tmp;
		}
	}
}
