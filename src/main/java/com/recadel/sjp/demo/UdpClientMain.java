package com.recadel.sjp.demo;

import com.recadel.sjp.discovery.SjpDiscovery;

import java.net.InetSocketAddress;

public class UdpClientMain {
	public static void main(String[] args) throws Exception {
		SjpDiscovery server = new SjpDiscovery();

		server.discover(address -> {
			System.out.println("Found address: " + address);
			server.close();
		}, new InetSocketAddress("192.168.16.255", 12345), 10, 3000L);
	}
}
