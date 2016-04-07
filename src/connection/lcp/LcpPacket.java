package connection.lcp;

import java.net.DatagramPacket;
import java.net.InetAddress;

import connection.Utilities;

public class LcpPacket implements Protocol {
	public static int HEADERLEN = 6;
	public static int VERSION = 1;
	public static int DEFAULTPORT = 1929;
	
	public static LcpPacket heartbeat() {
		LcpPacket lcpp = new LcpPacket(Utilities.broadcastAddress(), 
				Utilities.getBroadcastPort());;
		lcpp.setMessage("I'm alive!");
		return lcpp;
	}
	
	private DatagramPacket packet;
	private byte[] buffer = new byte[0];
	private byte[] header = new byte[HEADERLEN];
	private byte[] data = new byte[0];
	private InetAddress address;
	private int destinationPort;
	
	public LcpPacket(InetAddress destination, int port) {
		this.address = destination;
		this.destinationPort = port;
	}
	
	public LcpPacket(InetAddress destination) {
		this(destination, DEFAULTPORT);
	}
	
	public LcpPacket() {
		
	}
	
	public LcpPacket(DatagramPacket packet) {
		this(packet.getAddress(), packet.getPort());
		buffer = packet.getData();
		data = new byte[buffer.length - HEADERLEN];
		System.arraycopy(buffer, 0, header, 0, HEADERLEN);
		System.arraycopy(buffer, HEADERLEN, data, 0, data.length);
	}
	
	public InetAddress getAddress() {
		return address;
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
	
	public void setSyn() {
		setFlag(SYN);
	}

	public void syn() {
		return header[1].equals(SYN);
	}
	
	public void setFlag(int flag) {
		setFlag((byte) flag);
	}
	
	public void setFlag(byte flag) {
		header[1] = flag;
	}
	
	public void setDestination(InetAddress destination, int port) {
		this.address = destination;
		if (port != -1) {
			this.destinationPort = port;
		} else {
			this.destinationPort = DEFAULTPORT;
		}
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
		packet.setAddress(address);
		packet.setPort(destinationPort);
		return packet;
	}
	
	private void setVersion() {
		header[0] = (byte) VERSION;
	}

}
