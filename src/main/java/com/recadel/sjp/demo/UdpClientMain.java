package com.recadel.sjp.demo;

import com.recadel.sjp.discovery.SjpDiscoveryServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class UdpClientMain {
	public static void main(String[] args) throws Exception {
		SjpDiscoveryServer server = new SjpDiscoveryServer();

		server.discover(address -> {
			System.out.println("Found address: " + address);
		}, new InetSocketAddress("192.168.16.255", 12345), 10, 3000L);
	}
}
