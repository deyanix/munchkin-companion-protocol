package com.recadel.sjp.discover;

import com.recadel.sjp.connection.SjpMessageBuffer;

import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SjpDiscoveryServer {
	private final Map<SocketAddress, BlockingQueue<SjpMessageBuffer>> buffers = new ConcurrentHashMap<>();
	private final ExecutorService executorService = Executors.newFixedThreadPool(16);
	private DatagramSocket socket;
}
