package connection.lcp.state;

import java.util.HashMap;

import berryPicker.FileObject;
import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;
import connection.lcp.commands.Command;

public abstract class AbstractConnectionState implements ConnectionState {
	protected LcpConnection connection;
	protected FileObject fileObject;
	protected HashMap<ConnectionState, Command> transitionMap;
	
	public AbstractConnectionState(LcpConnection connection, FileObject fileObject) {
		this.connection = connection;
		this.fileObject = fileObject;
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
