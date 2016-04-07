package connection.lcp.commands;

import connection.lcp.state.ConnectionState;

public interface Command {
	public void runCommand(ConnectionState state);
}
