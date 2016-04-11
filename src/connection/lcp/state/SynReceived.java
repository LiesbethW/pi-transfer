package connection.lcp.state;

import berryPicker.FileObject;
import connection.lcp.LcpConnection;
import connection.lcp.Protocol;
import connection.lcp.commands.ProcessAck;
import connection.lcp.commands.ProcessFilePart;

public class SynReceived extends AbstractConnectionState {

	public SynReceived(LcpConnection connection, FileObject fileObject) {
		super(connection);
	}
	
	@Override
	protected void initializeTransitionMap() {
		transitionMap.put(Protocol.ACK, new ProcessAck());
		transitionMap.put(Protocol.FILE_PART, new ProcessFilePart());

	}

}
