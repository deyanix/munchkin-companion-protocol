package com.recadel.sjp.demo;

import com.recadel.sjp.discover.SjpIp4Address;
import com.recadel.sjp.discover.SjpIp4Network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
