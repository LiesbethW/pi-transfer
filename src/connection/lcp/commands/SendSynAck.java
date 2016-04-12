package connection.lcp.commands;

import connection.lcp.LcpPacket;
import connection.lcp.state.AbstractConnectionState;
import connection.lcp.state.ConnectionState;
import connection.lcp.state.SynReceived;

public class SendSynAck implements Command {

	@Override
	public Class<? extends AbstractConnectionState> runCommand(LcpPacket lcpp, ConnectionState state) {
		// Process the file and source information
		state.getFile().setName(lcpp.getFileName());
		state.getFile().setEmptyContent(lcpp.getTotalLength());
		state.getFile().setFileChecksum(lcpp.getFileChecksum());
		state.getFile().setBytesPerPart(lcpp.getBytesPerPart());
		
		// Create and send synack message
		LcpPacket synAckPacket = new LcpPacket();
		synAckPacket.setSynAck();
		
		// More steps necessary for encryption should go here
		// For now, confirm the sent file info by including it
		// in the SYN+ACK.
		synAckPacket.setMessage(lcpp.getMessage());
		state.completeAndSendPacket(synAckPacket);
		
		return SynReceived.class;
	}

}
