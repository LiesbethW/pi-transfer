package connection.lcp.state;

import berryPicker.FileObject;
import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;

public class Established extends AbstractConnectionState {
	
	public Established(LcpConnection connection, FileObject fileObject) {
		super(connection);
	}
	
	public Class<? extends AbstractConnectionState> digest(LcpPacket lcpp) {
		return this.getClass();
	}

	@Override
	protected void initializeTransitionMap() {
		// TODO Auto-generated method stub
		
	}

}
