package connection.lcp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.zip.CRC32;

import berryPicker.FileObject;
import connection.Utilities;

public class LcpPacket implements Protocol {
	
	private static CRC32 checksumCalculator = new CRC32();
	
	public static LcpPacket heartbeat() {
		LcpPacket lcpp = new LcpPacket(Utilities.broadcastAddress(), 
				Utilities.getBroadcastPort());;
		lcpp.setMessage("I'm alive!");
		lcpp.setHeartbeat();
		return lcpp;
	}
	
	private DatagramPacket packet;
	private byte[] buffer = new byte[0];
	private byte[] header = new byte[HEADERLEN];
	private byte[] data = new byte[0];
	private InetAddress address;
	private int destinationPort;
	
	private HashMap<String, String> options = new HashMap<String, String>();
	
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
		this.deSerializeMessage();
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
	
	public int getFlag() {
		return header[FLAG_FIELD] & 0xff;
	}
	
	public boolean fileTransferPacket() {
		return getFlag() < FILE_REQUEST;
	}
	
	public boolean syn() {
		return getFlag() == SYN;
	}
	
	public boolean synAck() {
		return getFlag() == SYN_ACK;
	}
	
	public boolean ack() {
		return getFlag() == ACK;
	}
	
	public boolean fin() {
		return getFlag() == FIN;
	}
	
	public boolean finAck() {
		return getFlag() == FIN_ACK;
	}
	
	public boolean fileRequest() {
		return getFlag() == FILE_REQUEST;
	}
	
	public boolean isHeartbeat() {
		return getFlag() == HEARTBEAT;
	}
	
	public short getVCID() {
		byte[] vcid = new byte[Short.BYTES];
		System.arraycopy(header, VCID_FIELD, vcid, 0, Short.BYTES);
		return ByteUtils.bytesToShort(vcid);
	}
	
	public String getFileName() {
		if (options.containsKey(FILENAME)) {
			return options.get(FILENAME);
		} else {
			return null;
		}
	}
	
	public long getFileChecksum() {
		if (options.containsKey(FILE_CHECKSUM)) {
			return Long.valueOf(options.get(FILE_CHECKSUM));
		} else {
			return 0;
		}
	}
	
	public int getTotalLength() {
		if (options.containsKey(TOTAL_LENGTH)) {
			return Integer.valueOf(options.get(TOTAL_LENGTH));
		} else {
			return 0;
		}
	}
	
	public void setSyn(FileObject file) {
		setFlag(SYN);
		setMessage(serializeFileInfo(file));
	}
	
	public void setSynAck() {
		setFlag(SYN_ACK);
	}
	
	public void setAck() {
		setFlag(ACK);
	}
	
	public void setFin() {
		setFlag(FIN);
	}
	
	public void setFinAck() {
		setFlag(FIN_ACK);
	}
	
	public void setFilePart() {
		setFlag(FILE_PART);
	}
	
	public void setFileRequest() {
		setFlag(FILE_REQUEST);
	}
	
	public void setHeartbeat() {
		setFlag(HEARTBEAT);
	}
	
	public void setVCID(short vcID) {
		byte[] virtualCircuitID = ByteUtils.shortToBytes(vcID);
		System.arraycopy(virtualCircuitID, 0, header, VCID_FIELD, virtualCircuitID.length);
	}
	
	public void setFlag(int flag) {
		header[FLAG_FIELD] = (byte) flag;
	}
	
	public void setSource() {
		header[SOURCE_FIELD] = Utilities.getMyInetAddress().getAddress()[3];
	}
	
	public void setDestination(InetAddress destination, int port) {
		this.address = destination;
		header[DESTINATION_FIELD] = address.getAddress()[3];
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
	
	private String serializeFileInfo(FileObject file) {
		String fileName = String.join(DELIMITER2, FILENAME, file.getName());
		String totalLength = String.join(DELIMITER2, TOTAL_LENGTH, String.valueOf(file.getLength()));
		checksumCalculator.reset();
		checksumCalculator.update(file.getContent());
		long checksum = checksumCalculator.getValue();
		String fileChecksum = String.join(DELIMITER2, FILE_CHECKSUM, String.valueOf(checksum));
		return String.join(DELIMITER, fileName, totalLength, fileChecksum);		
	}
	
	private void deSerializeMessage() {
		String[] info = getMessage().split(DELIMITER);
		for (int i = 0; i < info.length; i++) {
			String[] keyValuePair = info[i].split(DELIMITER2, 2);
			if (keyValuePair.length >= 2) {
				options.put(keyValuePair[0], keyValuePair[1]);
			} else {
				options.put(keyValuePair[0], null);
			}
		}
	}
	
	public void print() {
		System.out.println("-----------PACKET------------");
		System.out.println("-----------HEADER------------");
		System.out.format("Flag: %d\n", this.getFlag());
		System.out.format("Destination: %x\n", header[DESTINATION_FIELD]);
		System.out.format("Source: %s\n", this.getSource().toString());
		System.out.format("VCID: %d\n", this.getVCID());
		System.out.println("-----------CONTENT-----------");
		System.out.println(this.getMessage());
	}

}
