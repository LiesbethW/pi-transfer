package connection.lcp.state;

import java.util.HashMap;

import berryPicker.FileObject;
import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;
import connection.lcp.commands.Command;

public abstract class AbstractConnectionState implements ConnectionState {
	protected LcpConnection connection;
	protected FileObject fileObject;
	protected HashMap<Integer, Command> transitionMap = new HashMap<Integer, Command>();
	protected int maxTimeOuts = 2;
	protected int timeOuts = 0;
	
	
	public AbstractConnectionState(LcpConnection connection, FileObject fileObject) {
		this.connection = connection;
		this.fileObject = fileObject;
		initializeTransitionMap();
	}
	
	public Class<? extends AbstractConnectionState> digest(LcpPacket lcpp) {
		// Because a package has been received, the number of timeouts can
		// be reset to 0.
		timeOuts = 0;
		return transition(lcpp);
	}
	
	public void completeAndSendPacket(LcpPacket lcpp) {
		connection.completeAndSendPacket(lcpp);
	}
	
	protected Class<? extends AbstractConnectionState> transition(LcpPacket lcpp) {
		if (transitionMap.containsKey(lcpp.getFlag())) {
			return transitionMap.get(lcpp.getFlag()).runCommand(lcpp, null);
		} else {
			System.err.println("This state transition is not permitted.");
			return this.getClass();
		}
	}
	
	public boolean maxTimeoutsReached() {
		return timeOuts >= maxTimeOuts;
	}
	
	public LcpConnection getConnection() {
		return connection;
	}
	
	public FileObject getFile() {
		return fileObject;
	}
	
	protected abstract void initializeTransitionMap();
	
	protected void addTransition(Integer flag, Command command) {
		transitionMap.put(flag, command);
	}
	
}
