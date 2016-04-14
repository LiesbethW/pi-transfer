package connection.lcp.state;

import connection.lcp.LcpConnection;
import connection.lcp.Protocol;
import connection.lcp.commands.CloseConnection;
import connection.lcp.commands.ProcessFin;
import connection.lcp.commands.SendFin;

public class FinSent extends AbstractConnectionState {

	public FinSent(LcpConnection connection) {
		super(connection);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void initializeTransitionMap() {
		transitionMap.put(Protocol.FIN_ACK, new CloseConnection());
		transitionMap.put(Protocol.FIN, new ProcessFin());
		transitionMap.put(Protocol.FILE_PART_ACK, new SendFin());
		transitionMap.put(Protocol.FILE_PART, new SendFin());
	}

}
