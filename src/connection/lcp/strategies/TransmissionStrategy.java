package connection.lcp.strategies;

import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;

public abstract class TransmissionStrategy {
	private LcpConnection connection;
	
	public TransmissionStrategy(LcpConnection connection) {
		this.connection = connection;
	}
	
	public abstract void startTransmission();
	
	public abstract void handleFilePart(LcpPacket packet);
	
	public abstract void handleAck(LcpPacket packet);
	
	protected void sendAck(byte sequenceNumber) {
		LcpPacket ack = new LcpPacket();
		ack.setFilePartAck(sequenceNumber);
		this.connection().completeAndSendPacket(ack);
	}
	
	protected void sendPart(byte sequenceNumber, int partNumber) {
		if (connection.getFile().numberOfParts() > partNumber) {
			byte[] content = connection.getFile().getPart(partNumber);
			LcpPacket filePart = new LcpPacket();
			filePart.setFilePart(content, sequenceNumber);
			this.connection().completeAndSendPacket(filePart);
		}
	}
	
	protected void savePart(byte[] data, int partNumber) {
		System.out.format("Saving part %d (with length %d)\n", partNumber, data.length);
		connection.getFile().setPart(data, partNumber);
	}
	
	protected LcpConnection connection() {
		return connection;
	}
	
}
