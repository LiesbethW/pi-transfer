package lcp;

import java.net.DatagramPacket;
import java.net.InetAddress;

import udpConnection.UdpUtilities;

public class LcpPacket {
	public static int HEADERLEN = 6;
	public static int VERSION = 1;
	
	public static LcpPacket heartbeat() {
		LcpPacket lcpp = new LcpPacket(UdpUtilities.broadcastAddress(), 
				UdpUtilities.getBroadcastPort());;
		lcpp.setMessage("I'm alive!");
		return lcpp;
	}
	
	private DatagramPacket packet;
	private byte[] buffer = new byte[0];
	private byte[] header = new byte[HEADERLEN];
	private byte[] data = new byte[0];
	private InetAddress destination;
	private int destinationPort;
	
	public LcpPacket(InetAddress destination, int port) {
		this.destination = destination;
		this.destinationPort = port;
	}
	
	public LcpPacket(DatagramPacket packet) {
		this(packet.getAddress(), packet.getPort());
		buffer = packet.getData();
		data = new byte[buffer.length - HEADERLEN];
		System.arraycopy(buffer, 0, header, 0, HEADERLEN);
		System.arraycopy(buffer, HEADERLEN, data, 0, data.length);
	}
	
	public InetAddress getDestination() {
		return destination;
	}
	
	public int getPort() {
		return destinationPort;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public String getMessage() {
		return new String(getData());
	}
	
	public void setFlag(int flag) {
		header[1] = (byte) flag;
	}
	
	public void setMessage(String message) throws java.lang.ArrayIndexOutOfBoundsException {
		setData(message.getBytes());
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public DatagramPacket datagram() {
		setVersion();
		buffer = new byte[HEADERLEN + data.length];
		System.arraycopy(header, 0, buffer, 0, HEADERLEN);
		System.arraycopy(data, 0, buffer, HEADERLEN, data.length);
		packet = new DatagramPacket(buffer, buffer.length);
		packet.setAddress(destination);
		packet.setPort(destinationPort);
		return packet;
	}
	
	private void setVersion() {
		header[0] = (byte) VERSION;
	}

}
