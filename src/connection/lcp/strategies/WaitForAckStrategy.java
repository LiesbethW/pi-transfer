package connection.lcp.strategies;

import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;

public class WaitForAckStrategy extends TransmissionStrategy {
	byte lastSequenceNumberSent = -1;
	int lastPacketSent = -1;
	
	byte lastSequenceNumberReceived = -1;
	int lastPacketSaved = -1;
	
	public WaitForAckStrategy(LcpConnection connection) {
		super(connection);
	}
	
	public void startTransmission() {
		this.sendPart((byte) 0, 0);
		lastSequenceNumberSent = 0;
		lastPacketSent = 0;
	}
	
	@Override
	public void handleAck(LcpPacket packet) {
		if (packet.getSequenceNumber() == lastSequenceNumberSent) {
			this.sendPart(lastSequenceNumberSent++, lastPacketSent++);
		}

	}

	@Override
	public void handleFilePart(LcpPacket packet) {
		if (packet.getSequenceNumber() == (byte) lastSequenceNumberReceived + 1) {
			savePart(packet.getData(), lastPacketSaved + 1);
			lastSequenceNumberReceived++;
			lastPacketSaved++;
			sendAck(lastSequenceNumberReceived);
		}
	}

	@Override
	protected void updateWindow(byte ack) {
		// TODO Auto-generated method stub
		
	}

}
