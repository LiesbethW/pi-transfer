package overheadConnection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import lcp.LcpPacket;
import udpConnection.Client;
import udpConnection.UdpUtilities;

public class BroadcastConnection implements Runnable {
	private InetAddress bcastAddress;
	private Client UDPClient;
	private static long HEARTBEATINTERVAL = 10000;
	
	public BroadcastConnection() throws IOException {
		bcastAddress = UdpUtilities.broadcastAddress();
		int port = UdpUtilities.getBroadcastPort();
		UDPClient = new Client(bcastAddress, port);
	}
	
	public void run() {
		this.startHeartbeat();
		while (true) {
			listenForPackets();
		}
	}
	
	private void startHeartbeat() {
		Timer timer = new Timer();
		timer.schedule(new BeatHeart(this), new Date(), HEARTBEATINTERVAL);
	}
	
	private void listenForPackets() {
		while (true) {
			DatagramPacket packet = UDPClient.dequeuePacket(500);
			if (packet != null) {
				byte[] data = packet.getData();
				System.out.println(String.format("From %s, received: %s", 
						packet.getAddress().getHostAddress(), data.toString()));
			}
		}
	}
	
	private void sayHello() {
		UDPClient.send(LcpPacket.heartbeat());
	}
	
	/**
	 * A timer task that lets this broadcast
	 * @author liesbeth.wijers
	 *
	 */
	private class BeatHeart extends TimerTask {
		private BroadcastConnection berry;
		
		public BeatHeart(BroadcastConnection berry) {
			this.berry = berry;
		}
		
		public void run() {
			berry.sayHello();
		}
		
	}
	
}
