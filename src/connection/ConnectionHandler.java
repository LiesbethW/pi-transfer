package connection;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import connection.lcp.LcpPacket;

public class ConnectionHandler implements Runnable {
	private InetAddress bcastAddress;
	private Client UDPClient;
	private static long HEARTBEATINTERVAL = 10000;
	
	public ConnectionHandler() throws IOException {
		bcastAddress = Utilities.broadcastAddress();
		int port = Utilities.getBroadcastPort();
		UDPClient = new Client(port);
	}
	
	public void run() {
		this.startHeartbeat();
		while (true) {
			handlePackets();
		}
	}
	
	private void startHeartbeat() {
		Timer timer = new Timer();
		timer.schedule(new BeatHeart(this), new Date(), HEARTBEATINTERVAL);
	}
	
	private void handlePackets() {
		while (true) {
			LcpPacket packet = UDPClient.dequeuePacket(500);
			if (packet != null) {
				byte[] data = packet.getData();
				System.out.println(String.format("From %s, received: %s", 
						packet.getAddress(), new String(data)));
			}
		}
	}
	
	public void send(LcpPacket lcpp) {
		UDPClient.enqueue(lcpp);
	}
	
	private void sayHello() {
		this.send(LcpPacket.heartbeat());
	}
	
	/**
	 * A timer task that lets this broadcast
	 * @author liesbeth.wijers
	 *
	 */
	private class BeatHeart extends TimerTask {
		private ConnectionHandler berry;
		
		public BeatHeart(ConnectionHandler berry) {
			this.berry = berry;
		}
		
		public void run() {
			berry.sayHello();
		}
		
	}
	
}
