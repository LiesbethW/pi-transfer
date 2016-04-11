package connection.lcp.commands;

import berryPicker.FileObject;
import connection.lcp.LcpPacket;
import connection.lcp.state.AbstractConnectionState;
import connection.lcp.state.ConnectionState;
import connection.lcp.state.SynReceived;

public class SendSynAck implements Command {

	@Override
	public Class<? extends AbstractConnectionState> runCommand(LcpPacket lcpp, ConnectionState state) {
		// Process the file and source information
		FileObject file = state.getFile();
		file.setName(lcpp.getFileName());
		file.setEmptyContent(lcpp.getTotalLength());
		file.setFileChecksum(lcpp.getFileChecksum());
		
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
