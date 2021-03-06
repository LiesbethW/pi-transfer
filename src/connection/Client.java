package connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import connection.lcp.LcpPacket;

public class Client {
	private BlockingQueue<LcpPacket> receivedPacketQueue = new LinkedBlockingQueue<LcpPacket>();
	private BlockingQueue<LcpPacket> sendPacketQueue = new LinkedBlockingQueue<LcpPacket>();
	
	private int connectionPort;
	
	private DatagramSocket clientSocket = null;

	public Client(int port) {
		this.connectionPort = port;
		try {
			if (connectionPort != -1) {
				clientSocket = new DatagramSocket(connectionPort);
			} else {
				clientSocket = new DatagramSocket();
				connectionPort = clientSocket.getLocalPort();
			}
			new Thread(new PacketReceiver()).start();
			new Thread(new PacketSender()).start();
			Thread.sleep(100); // Give the communicator a chance
		} catch (IOException | InterruptedException e) { 
			e.printStackTrace();
		}
	}
	
	public Client(InetAddress destination) {
		this(-1);
	}

	/**
	 * Send data: package as LCP Packet and then
	 * use the send(LcpPacket lcpp) method.
	 * @param data
	 */
	public void enqueue(byte[] data, InetAddress destination) {
		LcpPacket lcpp = new LcpPacket(destination);
		lcpp.setData(data);
		enqueue(lcpp);
	}
	
	public void enqueue(LcpPacket lcpp) {
		sendPacketQueue.add(lcpp);
	}
	
	/**
	 * Send lcpp packet
	 * @param lcpp
	 */
	private void send(LcpPacket lcpp) {
		if (clientSocket != null) {
			try {
				clientSocket.send(lcpp.datagram());
			} catch (IOException e) {
				System.err.println("Couldn't write socket: " + e.getMessage());
			}
		} else {
			System.err.println("Didn't write socket: not connected");
		}		
	}

	/**
	 * Try if there is a packet on the queue: return it's data
	 * if that is so.
	 * @param timeout: Try for a limited number of milliseconds.
	 * @return
	 */
	public LcpPacket dequeuePacket(long timeout) {
		LcpPacket packet;
		
		try {
			packet = receivedPacketQueue.poll(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) { 
			return null; 
		}
		
		return packet;
	}

	public boolean hasPackets() {
		return !receivedPacketQueue.isEmpty();
	}

	class PacketReceiver implements Runnable {
		public void run() {
			try {
//				clientSocket.setSoTimeout(60 * 1000);

				while (true) {
					byte[] recvBuf = new byte[15000];
					DatagramPacket receivedPacket = new DatagramPacket(recvBuf, recvBuf.length);
					clientSocket.receive(receivedPacket);
					
					if (receivedPacket.getAddress().getHostAddress()
							.equals(Utilities.getMyInetAddress().getHostAddress())) {
						// Ignore my own packet
					} else {
						
						LcpPacket lcpp = new LcpPacket(receivedPacket);
						if (lcpp.checkChecksum()) {
							receivedPacketQueue.offer(lcpp);
						} else {
							lcpp.print();
							System.out.println("Packet checksum was wrong: drop packet");
						}
					}
				}
			} catch (IOException e) {
				System.err.println("Couldn't read socket: " + e.getMessage());
			} finally {
				clientSocket.close();
			}

			System.err.println("Communicator stopped!");
		}
	}
	
	class PacketSender implements Runnable {
		public void run() {
			try {
//				clientSocket.setSoTimeout(60 * 1000);

				while (true) {
					LcpPacket packet = null;
					long timeout = 500;
					
					try {
						packet = sendPacketQueue.poll(timeout, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) { 
						e.printStackTrace();
					}
					
					if (packet != null) {
						clientSocket.send(packet.datagram());
					}
					
				}
			} catch (IOException e) {
				System.err.println("Couldn't write to socket: " + e.getMessage());
			} finally {
				clientSocket.close();
			}

			System.err.println("Communicator stopped!");
		}
	}
	
	public void kill() {
		clientSocket.close();
	}
}
