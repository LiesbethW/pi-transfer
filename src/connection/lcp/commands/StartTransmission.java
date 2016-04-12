package connection.lcp.commands;

import connection.lcp.LcpPacket;
import connection.lcp.state.AbstractConnectionState;
import connection.lcp.state.ConnectionState;
import connection.lcp.state.Established;

public class StartTransmission implements Command {

	@Override
	public Class<? extends AbstractConnectionState> runCommand(LcpPacket lcpp, ConnectionState state) {

		LcpPacket ack = new LcpPacket();
		ack.setAck();
		state.completeAndSendPacket(ack);
		
		state.startTransmission();

		return Established.class;
	}

}
