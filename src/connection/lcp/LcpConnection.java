package connection.lcp;

import java.net.InetAddress;

import connection.Utilities;
import connection.lcp.state.ConnectionState;
import connection.lcp.state.Established;
import connection.lcp.state.Listening;

public class LcpConnection implements Runnable {
	
	private ConnectionState state;
	private InetAddress myIP;
	private InetAddress otherIP;
	
	public LcpConnection(InetAddress other) {
		myIP = Utilities.getMyInetAddress();
		this.otherIP = other;
		setState(new Listening());
		this.setup();
	}
	
	public void run() {

	}
	
	public boolean isEstablished() {
		return Established.class.isInstance(getState());
	}
	
	public ConnectionState getState() {
		return state;
	}
	
	private void setup() {
		
	}
	
	private void setState(ConnectionState state) {
		this.state = state;
	}
	
	
}
