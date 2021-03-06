package connection.lcp.strategies;

import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;

public class WaitForAckStrategy extends TransmissionStrategy {
	byte lastSequenceNumberSent;
	int lastPacketSent;
	
	byte lastSequenceNumberReceived;
	int lastPacketSaved;
	
	public WaitForAckStrategy(LcpConnection connection) {
		super(connection);
		lastSequenceNumberSent = -1;
		lastPacketSent = -1;
		
		lastSequenceNumberReceived = -1;
		lastPacketSaved = -1;
		
		System.out.format("Last sn received: %x\n", lastSequenceNumberReceived);
	}
	
	public void startTransmission() {
		System.out.format("Starting transmission of %s to %s\n", 
				connection().getFile().getName(), 
				connection().getReceiver().getHostAddress());
		
		this.sendPart((byte) 0, 0);
		lastSequenceNumberSent = 0;
		lastPacketSent = 0;
	}
	
	@Override
	public void handleAck(LcpPacket packet) {
		if (packet.getSequenceNumber() == lastSequenceNumberSent) {
			if (lastPacketSent >= connection().getFile().lastPart()) {
				this.connection().setTransmissionCompleted();
			} else {
				lastSequenceNumberSent++;
				lastPacketSent++;
				this.sendPart(lastSequenceNumberSent, lastPacketSent);
			}
		}

	}

	@Override
	public void handleFilePart(LcpPacket packet) {
		if (packet.getSequenceNumber() == (byte) (lastSequenceNumberReceived + 1)) {
			System.out.format("Saving sn %x as packet number %d\n", packet.getSequenceNumber(), lastPacketSaved + 1);
			savePart(packet.getData(), lastPacketSaved + 1);
			lastSequenceNumberReceived = packet.getSequenceNumber();
			lastPacketSaved++;
			this.sendAck(lastSequenceNumberReceived);
		}
	}

}
