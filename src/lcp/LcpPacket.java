package lcp;

import java.net.DatagramPacket;
import java.net.InetAddress;

import berryPicker.BerryUtilities;

public class LcpPacket {
	public static DatagramPacket heartbeat() {
		LcpPacket lcpp = new LcpPacket(BerryUtilities.broadcastAddress(), 
				BerryUtilities.getBroadcastPort());;
		lcpp.setMessage("I'm alive!");
		return lcpp.packet();
	}
	
	private DatagramPacket packet;
	private static int BUF_SIZE = 1500;
	
	public LcpPacket(InetAddress destination, int port) {
		byte[] buffer = new byte[BUF_SIZE];
		packet = new DatagramPacket(buffer, BUF_SIZE);
		packet().setAddress(destination);
		packet().setPort(port);
	}
	
	public void setMessage(String message) {
		packet().setData(message.getBytes());
	}
	
	public DatagramPacket packet() {
		return packet;
	}
}
