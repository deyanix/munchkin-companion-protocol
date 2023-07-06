package com.recadel.sjp.demo;

import com.recadel.sjp.discovery.SjpDiscoveryServer;

public class UdpServerMain {
	public static void main(String[] args) throws Exception {
		SjpDiscoveryServer server = new SjpDiscoveryServer(12345);

		server.start();
	}
}
