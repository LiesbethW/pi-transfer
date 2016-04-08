package connection.lcp.state;

import berryPicker.FileObject;
import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;

public class Established extends AbstractConnectionState {
	
	public Established(LcpConnection connection, FileObject fileObject) {
		super(connection, fileObject);
	}
	
	public ConnectionState digest(LcpPacket lcpp) {
		return this;
	}

	@Override
	protected void initializeTransitionMap() {
		// TODO Auto-generated method stub
		
	}
}
