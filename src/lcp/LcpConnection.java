package lcp;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import connectionState.ConnectionState;
import connectionState.Established;
import connectionState.Listening;

public class LcpConnection implements Runnable {
	
	private ConnectionState state;
	private DatagramSocket socket;
	private InetAddress myIP;
	
	public LcpConnection() {
		myIP = LcpUtilities.getInetAddress();
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
		try {
			socket = new DatagramSocket();		
			if (socket != null) {
				System.out.println(String.format("Created a connection on port %d, ip %s", 
						socket.getLocalPort(), socket.getLocalAddress().toString()));
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	private void setState(ConnectionState state) {
		this.state = state;
	}
	
	
}
