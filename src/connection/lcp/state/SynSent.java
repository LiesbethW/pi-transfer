package connection.lcp.state;

import berryPicker.FileObject;
import connection.lcp.LcpConnection;

public class SynSent extends AbstractConnectionState {

	public SynSent(LcpConnection connection, FileObject fileObject) {
		super(connection, fileObject);
	}
	
	@Override
	protected void initializeTransitionMap() {
		// TODO Auto-generated method stub

	}

}
