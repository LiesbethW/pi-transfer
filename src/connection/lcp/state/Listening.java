package connection.lcp.state;

import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;

public class Listening extends AbstractConnectionState {
	
	public Listening(LcpConnection connection) {
		super(connection);
	}
	
	public ConnectionState digest(LcpPacket lcpp) {
		return this;
	}

	@Override
	protected void initializeTransitionMap() {
		// TODO Auto-generated method stub
		
	}
}
