package com.recadel.sjp.demo;

import com.recadel.sjp.discovery.SjpDiscovery;

public class UdpServerMain {
	public static void main(String[] args) throws Exception {
		SjpDiscovery server = new SjpDiscovery(12345);
		server.start();
	}
}
