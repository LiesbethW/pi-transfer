package connection.lcp.state;

import java.util.HashMap;

import berryPicker.FileObject;
import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;
import connection.lcp.commands.Command;

public abstract class AbstractConnectionState implements ConnectionState {
	protected LcpConnection connection;
	protected HashMap<Integer, Command> transitionMap = new HashMap<Integer, Command>();
	protected int maxTimeOuts = 2;
	protected int timeOuts = 0;
	protected boolean downloadCompleted;
	protected boolean transmissionCompleted;
	
	public AbstractConnectionState(LcpConnection connection) {
		this.connection = connection;
		initializeTransitionMap();
	}
	
	public Class<? extends AbstractConnectionState> digest(LcpPacket lcpp) {
		// Because a package has been received, the number of timeouts can
		// be reset to 0.
		timeOuts = 0;
		return transition(lcpp);
	}
	
	public void handleAck(LcpPacket lcpp) {
		connection.getStrategy().handleAck(lcpp);
	}
	
	public void handleFilePart(LcpPacket lcpp) {
		connection.getStrategy().handleFilePart(lcpp);
	}
	
	public void completeAndSendPacket(LcpPacket lcpp) {
		connection.completeAndSendPacket(lcpp);
	}
	
	protected Class<? extends AbstractConnectionState> transition(LcpPacket lcpp) {
		if (transitionMap.containsKey(lcpp.getFlag())) {
			return transitionMap.get(lcpp.getFlag()).runCommand(lcpp, this);
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
		return connection.getFile();
	}
	
	public boolean downloadCompleted() {
		return downloadCompleted;
	}
	
	public boolean transmissionCompleted() {
		return transmissionCompleted;
	}
	
	public void setDownloadCompleted() {
		downloadCompleted = true;
	}
	
	public void setTransmissionCompleted() {
		transmissionCompleted = true;
	}
	
	protected abstract void initializeTransitionMap();
	
	protected void addTransition(Integer flag, Command command) {
		transitionMap.put(flag, command);
	}
	
}
