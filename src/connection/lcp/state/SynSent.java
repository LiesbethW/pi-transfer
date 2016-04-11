package connection.lcp.state;

import berryPicker.FileObject;
import connection.lcp.LcpConnection;
import connection.lcp.Protocol;
import connection.lcp.commands.StartTransmission;

public class SynSent extends AbstractConnectionState {

	public SynSent(LcpConnection connection, FileObject fileObject) {
		super(connection);
	}
	
	@Override
	protected void initializeTransitionMap() {
		transitionMap.put(Protocol.SYN_ACK, new StartTransmission());

	}

}
