package com.recadel.sjp.demo;

import com.recadel.sjp.reactnative.SjpDiscoveryManager;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class UdpClientMain {
	public static void main(String[] args) throws Exception {
		SjpDiscoveryManager manager = new SjpDiscoveryManager();
		manager.createClient(1, new InetSocketAddress(InetAddress.getByName("192.168.141.255"), 1923));
		manager.createServer(2, 1923);
	}
}
