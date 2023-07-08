package com.recadel.sjp.demo;

import com.recadel.sjp.discovery.SjpDiscoveryClient;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class UdpClientMain {
	public static void main(String[] args) throws Exception {
		SjpDiscoveryClient client = new SjpDiscoveryClient(Executors.newSingleThreadScheduledExecutor());

		client.discover(address -> {
			System.out.println("Found address: " + address);
			client.close();
		}, new InetSocketAddress("192.168.16.255", 12345), 10, 3000L);
	}
}
