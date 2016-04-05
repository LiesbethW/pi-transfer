package lcp;

import java.net.DatagramPacket;
import java.net.InetAddress;

import connectionState.ConnectionState;
import connectionState.Established;
import connectionState.Listening;
import udpConnection.Client;

public class LcpConnection implements Runnable {
	
	private ConnectionState state;
	private Client UDPclient;
	private InetAddress myIP;
	private InetAddress otherIP;
	
	public LcpConnection(InetAddress other) {
		myIP = LcpUtilities.getMyInetAddress();
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
		UDPclient = new Client(otherIP);
	}
	
	private void send(byte[] data) {
		UDPclient.send(data);
	}
	
	private DatagramPacket receiveData() {
		if (UDPclient.hasPackets()) {
			return UDPclient.dequeuePacket(500);
		} else {
			return null;
		}
		
	}
	
	private void setState(ConnectionState state) {
		this.state = state;
	}
	
	
}
