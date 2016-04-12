package connection.lcp.commands;

import connection.lcp.LcpPacket;
import connection.lcp.state.AbstractConnectionState;
import connection.lcp.state.ConnectionState;
import connection.lcp.state.Established;

public class ProcessFilePart implements Command {

	@Override
	public Class<? extends AbstractConnectionState> runCommand(LcpPacket lcpp, ConnectionState state) {
		
		state.handleFilePart(lcpp);
		
		return Established.class;
	}

}
