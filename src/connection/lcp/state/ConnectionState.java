package connection.lcp.state;

import connection.lcp.LcpPacket;

public interface ConnectionState {

	public abstract ConnectionState digest(LcpPacket lcpp);
	
	public abstract ConnectionState transition(ConnectionState state);
	
}
