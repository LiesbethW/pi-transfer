package connection.lcp.commands;

import connection.lcp.LcpPacket;
import connection.lcp.state.AbstractConnectionState;
import connection.lcp.state.ConnectionState;

public interface Command {
	
	public Class<? extends AbstractConnectionState> runCommand(LcpPacket lcpp, ConnectionState state);
	
}
