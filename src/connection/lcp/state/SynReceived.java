package connection.lcp.state;

import berryPicker.FileObject;
import connection.lcp.LcpConnection;

public class SynReceived extends AbstractConnectionState {

	public SynReceived(LcpConnection connection, FileObject fileObject) {
		super(connection, fileObject);
	}
	
	@Override
	protected void initializeTransitionMap() {
		// TODO Auto-generated method stub

	}

}
