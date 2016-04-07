package connection.lcp;

import java.net.InetAddress;

import berryPicker.FileObject;
import connection.ConnectionHandler;
import connection.Utilities;
import connection.lcp.state.ConnectionState;
import connection.lcp.state.Established;

public class LcpConnection implements Runnable {
	
	// LCP Connection attributes
	private ConnectionHandler handler;
	private ConnectionState state;
	private InetAddress myIP;
	private InetAddress otherIP;
	private byte[] dataToSend;
	private String fileName;
	
	// States
	private ConnectionState closed;
	private ConnectionState listening;
	private ConnectionState established;
	
	public LcpConnection(ConnectionHandler handler, FileObject file) {
		myIP = Utilities.getMyInetAddress();
		this.handler = handler;
		this.otherIP = file.getDestination();
		this.dataToSend = file.getContent();
		this.fileName = file.getName();
		initializeStates();
		setState(closed);
	}
	
	public void run() {
		// DO SOMETHING
	}
	
	public void sendThis(LcpPacket lcpp) {
		if (lcpp.getAddress() == null) {
			lcpp.setDestination(otherIP, -1);
		}
	}
	
	public boolean isEstablished() {
		return Established.class.isInstance(getState());
	}
	
	public ConnectionState getState() {
		return state;
	}
	
	public void digest(LcpPacket lcpp) {
		setState(getState().digest(lcpp));
	}
	
	private void setState(ConnectionState state) {
		this.state = state;
	}
	
	private void initializeStates() {
//		newClient = new NewClient(this);
//		readyToPlay = new ReadyToPlay(this);
//		waitingForOpponent = new WaitingForOpponent(this);
//		waitForChallengeResponse = new WaitForChallengeResponse(this);
//		challenged = new Challenged(this);
//		startPlaying = new StartPlaying(this);
//		playing = new Playing(this);
//		
//		HashSet<State> activeStates = new HashSet<>();
//		activeStates.addAll(Arrays.asList(readyToPlay, waitingForOpponent, 
//				waitForChallengeResponse, challenged, startPlaying, playing));
		
	}
	
	
}
