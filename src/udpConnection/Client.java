package udpConnection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import lcp.LcpPacket;

public class Client {
	private BlockingQueue<DatagramPacket> packetQueue = new LinkedBlockingQueue<DatagramPacket>();
	
	private InetAddress destination;
	private int connectionPort;
	
	private DatagramSocket clientSocket = null;

	public Client(InetAddress destination, int port) {
		this.destination = destination;
		this.connectionPort = port;
		try {
			new Thread(new Communicator()).start();
			Thread.sleep(100); // Give the communicator a chance
		} catch (InterruptedException e) { }
	}
	
	public Client(InetAddress destination) {
		this(destination, -1);
	}

	/**
	 * Send data: package as LCP Packet and then
	 * use the send(LcpPacket lcpp) method.
	 * @param data
	 */
	public void send(byte[] data) {
		LcpPacket lcpp = new LcpPacket(destination, connectionPort);
		lcpp.setData(data);
		send(lcpp);
	}
	
	/**
	 * Send lcpp packet
	 * @param lcpp
	 */
	public void send(LcpPacket lcpp) {
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
	public DatagramPacket dequeuePacket(long timeout) {
		DatagramPacket packet;
		
		try {
			packet = packetQueue.poll(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) { 
			return null; 
		}
		
		return packet;
	}

	public boolean hasPackets() {
		return !packetQueue.isEmpty();
	}

	class Communicator implements Runnable {
		public void run() {
			try {
				if (connectionPort != -1) {
					clientSocket = new DatagramSocket(connectionPort);
				} else {
					clientSocket = new DatagramSocket();
					connectionPort = clientSocket.getLocalPort();
				}
				
//				clientSocket.setSoTimeout(60 * 1000);

				while (true) {
					byte[] recvBuf = new byte[15000];
					DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
					clientSocket.receive(receivePacket);
					
					System.out.println("Received packet from" + receivePacket.getAddress().getHostAddress());
					
					packetQueue.offer(receivePacket);
				}
			} catch (IOException e) {
				System.err.println("Couldn't read socket: " + e.getMessage());
			} finally {
				clientSocket.close();
			}

			System.err.println("Communicator stopped!");
		}
	}
}
