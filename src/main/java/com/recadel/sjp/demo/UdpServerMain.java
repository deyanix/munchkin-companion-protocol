package com.recadel.sjp.demo;

import com.recadel.sjp.reactnative.SjpDiscoveryManager;

public class UdpServerMain {
	public static void main(String[] args) throws Exception {
		SjpDiscoveryManager manager = new SjpDiscoveryManager();
		manager.createServer(1, 1923);
	}
}
