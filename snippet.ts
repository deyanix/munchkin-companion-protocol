// @ts-nocheck
// -- Discovery Server (UDP)
const server: SjpDiscoveryServer = SjpModule.createDiscoveryServer({
	port: 12345,
	address: "192.168.16.255", // optional
});

server.close();

// -- Discovery Client (UDP)
const client: SjpDiscoveryClient = SjpModule.createDiscoveryClient({
	port: 12345,
	address: "192.168.16.255",
});

client.on('found', (found: SjpDiscoveryFound) => {
	const socket: SjpSocket = found.upgrade(); // start communication via TCP
	// ...
});

client.close();

// -- Socket client-side (TCP)
const socket: SjpSocket = SjpModule.createSocket({
	port: 12345,
	address: "192.168.16.49",
});

export interface SjpMessage {
	type: 'request' | 'response' | 'event';
	action: string;
	id: number;
	data: unknown;
}

socket.on('message', (message: SjpMessage) => {
	// ...
});

socket.on('error', (error: string) => {
	// ...
});

socket.on('close', () => {
	// ...
});

const message: SjpMessage = { /* ... */ };
socket.send(message);

socket.close();

// -- Socket server-side (TCP)
const server: SjpServerSocket = SjpModule.createServer({
	port: 12345,
	address: '192.168.16.49', // optional, default: 0.0.0.0
});

server.on('connect', (socket: SjpSocket) => {
	// ...
});

server.on('close', () => {
	// ...
});

server.close();
