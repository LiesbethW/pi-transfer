package connection;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import berryPicker.BerryHandler;
import berryPicker.FileObject;
import connection.lcp.ByteUtils;
import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;
import connection.lcp.LcpSender;

public class ConnectionHandler implements Transmitter, LcpSender {
	private static long HEARTBEATINTERVAL = 10000;
	
	private InetAddress bcastAddress;
	private InetAddress myAddress;
	private Client UDPClient;
	private GeneralCommunicator general;
	private HashMap<Short, LcpConnection> lcpConnections;
	private BerryHandler berryHandler;
	
	public ConnectionHandler(BerryHandler berryHandler) throws IOException {
		this.berryHandler = berryHandler;
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
		System.out.println("Starting transmission for file: " + file.getName());
		lcpc.start();
	}
	
	public void requestFile(String filename, InetAddress berry) {
		general.sendNewFileRequest(filename, berry);
	}
	
	private void startHeartbeat() {
		Timer timer = new Timer();
		timer.schedule(new BeatHeart(this), new Date(), HEARTBEATINTERVAL);
	}
	
	private void handlePackets() {
		LcpPacket packet = UDPClient.dequeuePacket(500);
		if (packet != null) {
			System.out.println(":: Received ::");
			packet.print();
			
			if (packet.isHeartbeat()) {
				int berryId = packet.getSourceId();
				Date timestamp = packet.getTimestamp();
				ArrayList<String> files = packet.getFileList();
				berryHandler.processHeartbeat(berryId, timestamp, files);
			} else if (!packet.fileTransferPacket() || packet.getDestination().equals(bcastAddress)) {
				general.process(packet);
			} else if (packet.getDestination().equals(myAddress)) {
				short vcid = packet.getVCID();
				if (lcpConnections.containsKey(vcid)) {
					System.out.format("Process with id %d and in state %s received packet\n",
							vcid, lcpConnections.get(vcid).getState().toString());
					lcpConnections.get(vcid).digest(packet);
				} else {
					System.out.println("VCID " + vcid + " is not yet known, I'm starting a new connection.");
					LcpConnection connection = this.createNewLcpConnection(vcid, packet.getSource());
					connection.digest(packet);
				}
			} else {
				System.out.println("This packet is not for me.");
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
				System.out.format("Download completed, saving file %s", file.getName());
				berryHandler.saveFile(file);
			}
			System.out.format("Removing connection %d\n", connectionToRemove);
			lcpConnections.remove(connectionToRemove);
		}
	}
	
	/**
	 * Send a packet to the client.
	 * @param lcpp
	 */
	public void send(LcpPacket lcpp) {
		UDPClient.enqueue(lcpp);
	}
	
	/**
	 * Create and transmit a heartbeat message
	 */
	private void sayHello() {
		LcpPacket heartbeat = new LcpPacket();
		heartbeat.setHeartbeat(berryHandler.listLocalFiles());
		this.send(heartbeat);
	}
	
	/**
	 * Generate a vcid: it is a short (two bytes), of which the
	 * first is the id of this device and the second a random byte
	 * that is not in use yet. This guarantees a unique vcid for this
	 * network.
	 * @return
	 */
	short generateVCID() {
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
	 * Package private method to use berryHandler
	 * @return
	 */
	BerryHandler berryHandler() {
		return this.berryHandler;
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
