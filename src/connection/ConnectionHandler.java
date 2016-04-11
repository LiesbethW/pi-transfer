package connection;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import berryPicker.FileObject;
import connection.lcp.ByteUtils;
import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;

public class ConnectionHandler implements Runnable {
	private static long HEARTBEATINTERVAL = 10000;
	
	private InetAddress bcastAddress;
	private Client UDPClient;
	private HashMap<Short, LcpConnection> lcpConnections;
	
	public ConnectionHandler() throws IOException {
		bcastAddress = Utilities.broadcastAddress();
		int port = Utilities.getBroadcastPort();
		UDPClient = new Client(port);
		lcpConnections = new HashMap<Short, LcpConnection>();
	}
	
	public void run() {
		this.startHeartbeat();
		while (true) {
			handlePackets();
		}
	}
	
	public void transmitFile(FileObject file) {
		LcpConnection lcpc = this.createNewLcpConnection();
		lcpc.setFile(file);
		Thread lcpThread = new Thread(lcpc);
		lcpThread.start();
	}
	
	private void startHeartbeat() {
		Timer timer = new Timer();
		timer.schedule(new BeatHeart(this), new Date(), HEARTBEATINTERVAL);
	}
	
	private void handlePackets() {
		while (true) {
			LcpPacket packet = UDPClient.dequeuePacket(500);
			if (packet != null) {
				packet.print();
			}
		}
	}
	
	public void send(LcpPacket lcpp) {
		UDPClient.enqueue(lcpp);
	}
	
	private void sayHello() {
		this.send(LcpPacket.heartbeat());
	}
	
	private short generateVCID() {
		short vcid = 0;
		while (vcid == 0) {
			byte[] vCIDbytes = new byte[Short.BYTES];
			new Random().nextBytes(vCIDbytes);
			vCIDbytes[0] = Utilities.getMyInetAddress().getAddress()[3];
			short possibleVCID = ByteUtils.bytesToShort(vCIDbytes);
			if (!lcpConnections.containsKey(possibleVCID)) {
				vcid = possibleVCID;
			}
		}
		return vcid;
	}
	
	private LcpConnection createNewLcpConnection() {
		return createNewLcpConnection(generateVCID());
	}
	
	private LcpConnection createNewLcpConnection(Short vcid) {
		lcpConnections.put(vcid, new LcpConnection(this, null, vcid));
		return lcpConnections.get(vcid);
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
