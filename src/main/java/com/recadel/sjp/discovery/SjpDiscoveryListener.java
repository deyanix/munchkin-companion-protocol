package com.recadel.sjp.discovery;

import java.net.SocketAddress;

public interface SjpDiscoveryListener {
	void onDiscover(SocketAddress address);
}
