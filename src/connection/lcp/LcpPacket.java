package connection.lcp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.zip.CRC32;

import berryPicker.FileObject;
import connection.Utilities;

public class LcpPacket implements Protocol {
	
	private static CRC32 checksumCalculator = new CRC32();
	private static DateFormat dateFormat = DateFormat.getDateInstance();
	
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
		// Read the header
		buffer = packet.getData();
		System.arraycopy(buffer, 0, header, 0, HEADERLEN);
		
		data = new byte[getContentLength()];
		System.arraycopy(buffer, HEADERLEN, data, 0, data.length);
		
		// Make sure all header bytes and the like have been set before this
		buffer = new byte[HEADERLEN + data.length];
		System.arraycopy(header, 0, buffer, 0, HEADERLEN);
		System.arraycopy(data, 0, buffer, HEADERLEN, data.length);
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
	
	public int getSourceId() {
		return (int) 0xff & header[SOURCE_FIELD];
	}
	
	public InetAddress getSource() {
		try {
			String ip = String.format("%s.%d", Utilities.IP_RANGE, getSourceId());
			return InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			return null;
		}
	}
	
	public int getDestinationId() {
		return (int) 0xff & header[DESTINATION_FIELD];
	}
	
	public InetAddress getDestination() {
		try {
			String ip = String.format("%s.%d", Utilities.IP_RANGE, getDestinationId());
			return InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			return null;
		}
	}
	
	public int getFlag() {
		return header[FLAG_FIELD] & 0xff;
	}
	
	public boolean fileTransferPacket() {
		return getVCID() != 0;
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
	
	public boolean filePart() {
		return getFlag() == FILE_PART;
	}
	
	public boolean filePartAck() {
		return getFlag() == FILE_PART_ACK;
	}
	
	public boolean isHeartbeat() {
		return getFlag() == HEARTBEAT;
	}
	
	public short getVCID() {
		byte[] vcid = new byte[Short.BYTES];
		System.arraycopy(header, VCID_FIELD, vcid, 0, Short.BYTES);
		return ByteUtils.bytesToShort(vcid);
	}
	
	public short getContentLength() {
		byte[] contentLength = new byte[Short.BYTES];
		System.arraycopy(header, CONTENT_LENGTH, contentLength, 0, Short.BYTES);
		return ByteUtils.bytesToShort(contentLength);
	}
	
	public byte getSequenceNumber() {
		return header[SEQUENCE_NUMBER];
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
			String checksumString = options.get(FILE_CHECKSUM).trim();
			return Long.parseLong(checksumString);
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
	
	public int getBytesPerPart() {
		if (options.containsKey(BYTES_PER_PART)) {
			return Integer.valueOf(options.get(BYTES_PER_PART));
		} else {
			return FileObject.DEFAULT_LENGTH;
		}
	}
	
	public Date getTimestamp() {
		if (options.containsKey(TIMESTAMP)) {
			return new Date(Long.valueOf((options.get(TIMESTAMP))));
		} else {
			return new Date();
		}
	}
	
	public ArrayList<String> getFileList() {
		if (options.containsKey(FILES)) {
			ArrayList<String> files = new ArrayList<String>
				(Arrays.asList(options.get(FILES).split(DELIMITER3)));
			return files;
		} else {
			return new ArrayList<String>();
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
	
	public void setFilePart(byte[] filePart, byte sequenceNumber) {
		setFlag(FILE_PART);
		header[SEQUENCE_NUMBER] = sequenceNumber;
		this.setData(filePart);
	}
	
	public void setFilePartAck(byte sequenceNumber) {
		setFlag(FILE_PART_ACK);
		header[SEQUENCE_NUMBER] = sequenceNumber;
	}
	
	/**
	 * File request for a new download
	 * @param filename
	 * @param encryption
	 */
	public void setFileRequest(String filename, boolean encryption) {
		setFlag(FILE_REQUEST);
		int offset = 0;
		this.setMessage(this.serializeFileRequest(filename, offset, encryption));
	}
	
	/**
	 * File request to resume an existing download
	 * @param vcid
	 * @param filename
	 * @param offset
	 * @param encryption
	 */
	public void setFileRequest(short vcid, String filename, int offset, boolean encryption) {
		setFlag(FILE_REQUEST);
		setVCID(vcid);
		this.setMessage(this.serializeFileRequest(filename, offset, encryption));
	}
	
	public void setHeartbeat(ArrayList<String> files) {
		setFlag(HEARTBEAT);
		this.setSource();
		this.setDestination(Utilities.broadcastAddress(), Utilities.getBroadcastPort());
		this.setMessage(this.serializeHeartbeatInfo(files));
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
		// Set version
		setVersion();
		
		// Set content length
		setContentLength();
		
		// Make sure all header bytes and the like have been set before this
		buffer = new byte[HEADERLEN + data.length];
		System.arraycopy(header, 0, buffer, 0, HEADERLEN);
		System.arraycopy(data, 0, buffer, HEADERLEN, data.length);
		
		// Set the checksum
		setChecksum();
		
		packet = new DatagramPacket(buffer, buffer.length);
		packet.setAddress(address);
		packet.setPort(destinationPort);
		return packet;
	}
	
	private void setVersion() {
		header[0] = (byte) VERSION;
	}
	
	private void setContentLength() {
		byte[] contentLength = ByteUtils.shortToBytes((short) this.getData().length);
		System.arraycopy(contentLength, 0, header, CONTENT_LENGTH, contentLength.length);
	}
	
	public boolean checkChecksum() {
		long sentChecksum = getChecksum();
		cleanChecksumValue();
		checksumCalculator.reset();
		checksumCalculator.update(buffer);
		return sentChecksum == checksumCalculator.getValue();
	}
	
	private long getChecksum() {
		byte[] checksumBytes = new byte[4];
		System.arraycopy(buffer, PACKET_CHECKSUM_FIELD, checksumBytes, 0, checksumBytes.length);
		return ByteUtils.fourBytesToLong(checksumBytes);
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
		byte[] checksumBytes = ByteUtils.longToFourBytes(checksum);
		System.arraycopy(checksumBytes, 0, buffer, PACKET_CHECKSUM_FIELD, checksumBytes.length);
	}
	
	private String serializeFileInfo(FileObject file) {
		String fileName = String.join(DELIMITER2, FILENAME, file.getName());
		String totalLength = String.join(DELIMITER2, TOTAL_LENGTH, String.valueOf(file.getLength()));
		String fileChecksum = String.join(DELIMITER2, FILE_CHECKSUM, String.valueOf(file.getFileChecksum()));
		String bytesPerPartString = String.join(DELIMITER2, BYTES_PER_PART, String.valueOf(file.getBytesPerPart()));
		return String.join(DELIMITER, fileName, totalLength, fileChecksum, bytesPerPartString);		
	}
	
	private String serializeFileRequest(String filename, int offset, boolean encryption) {
		String filenameString = String.join(DELIMITER2, FILENAME, filename);
		String offsetString = String.join(DELIMITER2, OFFSET, String.valueOf(offset));
		String encryptionPlease = String.join(DELIMITER2, ENCRYPTION, String.valueOf(encryption));
		return String.join(DELIMITER, filenameString, offsetString, encryptionPlease);
	}
	
	private String serializeHeartbeatInfo(ArrayList<String> files) {
		String timestampString = String.join(DELIMITER2, TIMESTAMP, String.valueOf((new Date()).getTime()));
		String fileList = String.join(DELIMITER3, files);
		String fileListString = String.join(DELIMITER2, FILES, fileList);
		return String.join(DELIMITER, timestampString, fileListString);
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
		System.out.format("Destination: %s\n", this.getDestination().toString());
		System.out.format("Source: %s\n", this.getSource().toString());
		System.out.format("VCID: %d\n", this.getVCID());
		System.out.format("Content length: %d\n", this.getContentLength());
		System.out.format("Sequence Number: %d\n", this.getSequenceNumber());
		System.out.println("-----------CONTENT-----------");
		System.out.println(this.getMessage());
	}

}
