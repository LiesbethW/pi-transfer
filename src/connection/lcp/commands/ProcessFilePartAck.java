package connection.lcp.commands;

import connection.lcp.LcpPacket;
import connection.lcp.state.AbstractConnectionState;
import connection.lcp.state.ConnectionState;
import connection.lcp.state.Established;

public class ProcessFilePartAck implements Command {

	@Override
	public Class<? extends AbstractConnectionState> runCommand(LcpPacket lcpp, ConnectionState state) {
		
		state.getConnection().handleAck(lcpp);
		
		if (state.transmissionCompleted()) {
			return (new SendFin()).runCommand(lcpp, state);
		} else {
			return Established.class;
		}
	}

}
