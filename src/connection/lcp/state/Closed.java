package connection.lcp.state;

import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;

public class Closed extends AbstractConnectionState {

	public Closed(LcpConnection connection) {
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
