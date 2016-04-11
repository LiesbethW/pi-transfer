package connection.lcp.state;

import berryPicker.FileObject;
import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;
import connection.lcp.Protocol;
import connection.lcp.commands.ProcessFilePart;
import connection.lcp.commands.ProcessFilePartAck;

public class Established extends AbstractConnectionState {
	
	public Established(LcpConnection connection, FileObject fileObject) {
		super(connection);
	}
	
	public Class<? extends AbstractConnectionState> digest(LcpPacket lcpp) {
		return this.getClass();
	}

	@Override
	protected void initializeTransitionMap() {
		transitionMap.put(Protocol.FILE_PART, new ProcessFilePart());
		transitionMap.put(Protocol.FILE_PART_ACK, new ProcessFilePartAck());
	}

}
