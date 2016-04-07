package connection.lcp.state;

import java.util.HashMap;

import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;
import connection.lcp.commands.Command;

public abstract class AbstractConnectionState implements ConnectionState {
	protected LcpConnection connection;
	protected HashMap<ConnectionState, Command> transitionMap;
	
	public AbstractConnectionState(LcpConnection connection) {
		this.connection = connection;
		initializeTransitionMap();
	}
	
	public abstract ConnectionState digest(LcpPacket lcpp);
	
	public ConnectionState transition(ConnectionState state) {
		if (transitionMap.containsKey(state)) {
			transitionMap.get(state).runCommand(state);
			return state;
		} else {
			System.err.println("This state transition is not permitted.");
			return this;
		}
	}
	
	protected abstract void initializeTransitionMap();

}
