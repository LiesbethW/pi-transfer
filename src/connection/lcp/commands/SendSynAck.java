package connection.lcp.commands;

import connection.lcp.LcpPacket;
import connection.lcp.state.AbstractConnectionState;
import connection.lcp.state.ConnectionState;

public class SendSynAck implements Command {

	@Override
	public Class<? extends AbstractConnectionState> runCommand(LcpPacket lcpp, ConnectionState state) {
		// TODO Auto-generated method stub
		return null;
	}

}
