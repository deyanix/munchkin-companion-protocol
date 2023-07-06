package com.recadel.sjp.discover;

import java.net.SocketAddress;

public interface SjpDiscoverListener {
	void onDiscover(SocketAddress address);
}
