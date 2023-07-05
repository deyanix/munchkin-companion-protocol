package com.recadel.sjp.demo;

import com.recadel.munchkincompanion.protocol.MunchkinClient;
import com.recadel.sjp.connection.SjpIp4Address;
import com.recadel.sjp.connection.SjpIp4Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ScanMain {
	public static void main(String[] args) throws Exception {
		ExecutorService executorService = Executors.newFixedThreadPool(32);

		SjpIp4Address address = new SjpIp4Address(192, 168, 16, 24);
		SjpIp4Address subnet = new SjpIp4Address(255, 255, 0, 0);
		SjpIp4Network network = new SjpIp4Network(address, subnet);

//		executorService.invokeAll(StreamSupport
//				.stream(network.spliterator(), false)
//				.map(addr -> (Callable<?>) () -> {
//					Socket socket = null;
//					try {
//						InetAddress inetAddress = InetAddress.getByAddress(addr.getBytes());
//						System.out.println(inetAddress);
//						socket = new Socket();
//						socket.connect(new InetSocketAddress(inetAddress, 1234), 1000);
//
//						BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//						String text;
//						while ((text = reader.readLine()) != null) {
//							System.out.println("HAHAHA! " + text);
//						}
//
//					} catch (IOException e) {
//						throw new RuntimeException(e);
//					} finally {
//						assert socket != null;
//						socket.close();
//					}
//					return null;
//				})
//				.collect(Collectors.toList()), 66, TimeUnit.SECONDS);
	}
}
