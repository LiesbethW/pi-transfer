package connection.lcp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.zip.CRC32;

import connection.Utilities;

public class LcpPacket implements Protocol {
	
	private static CRC32 checksumCalculator = new CRC32();
	
	public static LcpPacket heartbeat() {
		LcpPacket lcpp = new LcpPacket(Utilities.broadcastAddress(), 
				Utilities.getBroadcastPort());;
		lcpp.setMessage("I'm alive!");
		lcpp.setFlag(HEARTBEAT);
		return lcpp;
	}
	
	private DatagramPacket packet;
	private byte[] buffer = new byte[0];
	private byte[] header = new byte[HEADERLEN];
	private byte[] data = new byte[0];
	private InetAddress address;
	private int destinationPort;
	
	public LcpPacket(InetAddress destination, int port) {
		this.setDestination(destination, port);
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
	
	public InetAddress getSource() {
		try {
			String ip = String.format("%s.%d", Utilities.IP_RANGE, (int) 0xff & header[2]);
			return InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			return null;
		}
	}
	
	public void setSyn() {
		setFlag(SYN);
	}

	public boolean syn() {
		return (header[FLAG_FIELD]^SYN) == 0;
	}
	
	public void setFlag(byte flag) {
		header[FLAG_FIELD] = flag;
	}
	
	public void setSource() {
		header[SOURCE_FIELD] = Utilities.getMyInetAddress().getAddress()[3];
	}
	
	public void setDestination(InetAddress destination, int port) {
		this.address = destination;
		header[3] = address.getAddress()[3];
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
		
		// Make sure all header bytes and the like have been set before this
		buffer = new byte[HEADERLEN + data.length];
		System.arraycopy(header, 0, buffer, 0, HEADERLEN);
		System.arraycopy(data, 0, buffer, HEADERLEN, data.length);
		setChecksum();
		
		packet = new DatagramPacket(buffer, buffer.length);
		packet.setAddress(address);
		packet.setPort(destinationPort);
		return packet;
	}
	
	private void setVersion() {
		header[0] = (byte) VERSION;
	}
	
	public boolean checkChecksum() {
		long sentChecksum = getChecksum();
		cleanChecksumValue();
		checksumCalculator.reset();
		checksumCalculator.update(buffer);
		long checksum = checksumCalculator.getValue();
		return sentChecksum == checksumCalculator.getValue();
	}
	
	private long getChecksum() {
		byte[] checksumBytes = new byte[Long.BYTES];
		System.arraycopy(buffer, PACKET_CHECKSUM_FIELD, checksumBytes, 0, checksumBytes.length);
		return ByteUtils.bytesToLong(checksumBytes);
	}
	
	private void setChecksum() {
		// Set the checksum field to 0 to compute the checksum over the
		// 'clean' packet
		cleanChecksumValue();
		
		checksumCalculator.reset();
		checksumCalculator.update(buffer);
		long checksum = checksumCalculator.getValue();
		setChecksumField(checksum);
	}
	
	private void cleanChecksumValue() {
		setChecksumField(0);
	}
	
	private void setChecksumField(long checksum) {
		byte[] checksumBytes = ByteUtils.longToBytes(checksum);
		System.arraycopy(checksumBytes, 0, buffer, PACKET_CHECKSUM_FIELD, checksumBytes.length);
	}

}
