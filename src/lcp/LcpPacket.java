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
	private static int BUF_SIZE = 1500;
	private byte[] buffer = new byte[BUF_SIZE];
	private byte[] header = new byte[HEADERLEN];
	
	public LcpPacket(InetAddress destination, int port) {
		packet = new DatagramPacket(buffer, BUF_SIZE);
		packet().setAddress(destination);
		packet().setPort(port);
		initHeader();
	}
	
	public void setFlag(int flag) {
		header[1] = (byte) flag;
		writeHeader();
	}
	
	public void setMessage(String message) throws java.lang.ArrayIndexOutOfBoundsException {
		setData(message.getBytes());
	}
	
	public void setData(byte[] data) {
		buffer = new byte[HEADERLEN + data.length];
		System.arraycopy(data, 0, buffer, HEADERLEN, data.length);
		packet().setData(buffer);
	}
	
	public DatagramPacket packet() {
		return packet;
	}
	
	private void initHeader() {
		header[0] = (byte) VERSION;
		writeHeader();
	}
	
	private void writeHeader() {
		System.arraycopy(header, 0, buffer, 0, HEADERLEN);
		packet().setData(buffer);
	}
}
