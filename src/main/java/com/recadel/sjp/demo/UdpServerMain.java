package com.recadel.sjp.demo;

import com.recadel.sjp.discovery.SjpDiscoveryServer;

import java.util.concurrent.Executors;

public class UdpServerMain {
	public static void main(String[] args) throws Exception {
		SjpDiscoveryServer server = new SjpDiscoveryServer(Executors.newSingleThreadScheduledExecutor(),12345);
		server.start();
	}
}
