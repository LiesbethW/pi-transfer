package connection.lcp.commands;

import connection.lcp.LcpPacket;
import connection.lcp.state.AbstractConnectionState;
import connection.lcp.state.ConnectionState;
import connection.lcp.state.FinSent;

public class ProcessAck implements Command {

	@Override
	public Class<? extends AbstractConnectionState> runCommand(LcpPacket lcpp, ConnectionState state) {
		if (state.transmissionCompleted() || state.downloadCompleted()) {
			LcpPacket fin = new LcpPacket();
			fin.setFin();
			state.completeAndSendPacket(fin);
			return FinSent.class;
		}
		return null;
	}

}
