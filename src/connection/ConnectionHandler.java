package connection;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import berryPicker.FileObject;
import berryPicker.Transmitter;
import connection.lcp.ByteUtils;
import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;

public class ConnectionHandler implements Runnable {
	private static long HEARTBEATINTERVAL = 10000;
	
	private InetAddress bcastAddress;
	private InetAddress myAddress;
	private Client UDPClient;
	private GeneralCommunicator general;
	private HashMap<Short, LcpConnection> lcpConnections;
	private Transmitter transmitter;
	
	public ConnectionHandler(Transmitter transmitter) throws IOException {
		this.transmitter = transmitter;
		bcastAddress = Utilities.broadcastAddress();
		myAddress = Utilities.getMyInetAddress();
		int port = Utilities.getBroadcastPort();
		UDPClient = new Client(port);
		general = new GeneralCommunicator(this);
		lcpConnections = new HashMap<Short, LcpConnection>();
	}
	
	public void run() {
		this.startHeartbeat();
		while (true) {
			handlePackets();
			checkConnections();
		}
	}
	
	public void transmitFile(FileObject file) {
		LcpConnection lcpc = this.createNewLcpConnection(file);
		System.out.println("File with name " + file.getName());
		System.out.println("And content " + (new String(file.getContent())));
		lcpc.start();
	}
	
	public void requestFile(String filename, InetAddress berry) {
		general.sendFileRequest(filename, berry);
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
				
				if (packet.isHeartbeat()) {
					int berryId = packet.getSourceId();
					Date timestamp = packet.getTimestamp();
					ArrayList<String> files = packet.getFileList();
					transmitter.processHeartbeat(berryId, timestamp, files);
				} else if (packet.getDestination().equals(bcastAddress)) {
					general.process(packet);
				} else if (packet.getDestination().equals(myAddress)) {
					short vcid = packet.getVCID();
					if (lcpConnections.containsKey(vcid)) {
						lcpConnections.get(vcid).digest(packet);
					} else {
						System.out.println("VCID " + vcid + " is not yet known, I'm starting a new connection.");
						LcpConnection connection = this.createNewLcpConnection(vcid, packet.getAddress());
						connection.digest(packet);
					}
				} else {
					System.out.println("This packet is not for me:");
					packet.print();
				}
				
			}
		}
	}
	
	/**
	 * Check for closed connection and remove those from the
	 * active list. Save the file if it completed downloading.
	 */
	private void checkConnections() {
		short connectionToRemove = -1;
		for (Short vcid : lcpConnections.keySet()) {
			if (lcpConnections.get(vcid).isClosed()) {
				connectionToRemove = vcid;
				break;
			}
		}
		if (connectionToRemove != -1) { 
			if (lcpConnections.get(connectionToRemove).downloadCompleted()) {
				FileObject file = lcpConnections.get(connectionToRemove).getFile();
				transmitter.saveFile(file);
			}
			lcpConnections.remove(connectionToRemove);
		}
	}
	
	public void send(LcpPacket lcpp) {
		UDPClient.enqueue(lcpp);
	}
	
	private void sayHello() {
		LcpPacket heartbeat = new LcpPacket();
		heartbeat.setHeartbeat(transmitter.listLocalFiles());
		this.send(heartbeat);
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
	
	/**
	 * Create a new LcpConnection for a file with destination
	 * that you wish to send
	 * @param file
	 * @return
	 */
	private LcpConnection createNewLcpConnection(FileObject file) {
		return createNewLcpConnection(file, generateVCID(), file.getDestination());
	}
	
	/**
	 * Create a new LcpConnection based on an incoming packet
	 * @param vcid
	 * @param address
	 * @return
	 */
	private LcpConnection createNewLcpConnection(Short vcid, InetAddress address) {
		return createNewLcpConnection(null, vcid, address);
	}
	
	private LcpConnection createNewLcpConnection(FileObject file, Short vcid, InetAddress address) {
		lcpConnections.put(vcid, new LcpConnection(this, file, vcid, address));
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
	
	/**
	 * For testing
	 */
	public Client getClient() {
		return UDPClient;
	}
	
}
