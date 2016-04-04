package berryPicker;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import lcp.LcpPacket;

public class BroadcastBerry {
	private InetAddress bcastAddress;
	private DatagramSocket socket;
	private static long HEARTBEATINTERVAL = 10000;
	
	public BroadcastBerry() throws IOException {
		bcastAddress = BerryUtilities.broadcastAddress();
		int port = BerryUtilities.getBroadcastPort();
		socket = new DatagramSocket(port);
		this.startHeartbeat();
	}
	
	public DatagramSocket getSocket() {
		return socket;
	}
	
	private void startHeartbeat() {
		Timer timer = new Timer();
		timer.schedule(new BeatHeart(this), new Date(), HEARTBEATINTERVAL);
	}
	
	private void sayHello() {
		try {
			this.getSocket().send(LcpPacket.heartbeat());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class BeatHeart extends TimerTask {
		private BroadcastBerry berry;
		
		public BeatHeart(BroadcastBerry berry) {
			this.berry = berry;
		}
		
		public void run() {
			berry.sayHello();
		}
		
	}
	
}
