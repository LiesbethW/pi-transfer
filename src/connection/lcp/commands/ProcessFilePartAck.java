package connection.lcp.commands;

import connection.lcp.LcpPacket;
import connection.lcp.state.AbstractConnectionState;
import connection.lcp.state.ConnectionState;
import connection.lcp.state.Established;
import connection.lcp.state.FinSent;

public class ProcessFilePartAck implements Command {

	@Override
	public Class<? extends AbstractConnectionState> runCommand(LcpPacket lcpp, ConnectionState state) {
		
		state.handleAck(lcpp);
		
		if (state.transmissionCompleted()) {
			LcpPacket fin = new LcpPacket();
			fin.setFin();
			state.completeAndSendPacket(fin);
			return FinSent.class;
		} else {
			return Established.class;
		}
	}

}
