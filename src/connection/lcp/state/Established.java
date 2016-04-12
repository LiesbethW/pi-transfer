package connection.lcp.state;

import connection.lcp.LcpConnection;
import connection.lcp.Protocol;
import connection.lcp.commands.ProcessFilePart;
import connection.lcp.commands.ProcessFilePartAck;
import connection.lcp.commands.ProcessFin;

public class Established extends AbstractConnectionState {
	
	public Established(LcpConnection connection) {
		super(connection);
	}

	@Override
	protected void initializeTransitionMap() {
		transitionMap.put(Protocol.FILE_PART, new ProcessFilePart());
		transitionMap.put(Protocol.FILE_PART_ACK, new ProcessFilePartAck());
		transitionMap.put(Protocol.FIN, new ProcessFin());
	}

}
