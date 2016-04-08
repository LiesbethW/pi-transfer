package connection.lcp.commands;

import connection.lcp.LcpPacket;
import connection.lcp.state.AbstractConnectionState;
import connection.lcp.state.ConnectionState;
import connection.lcp.state.SynSent;

public class SendSyn implements Command {

	@Override
	public Class<? extends AbstractConnectionState> runCommand(LcpPacket lcpp, ConnectionState state) {
		LcpPacket synPacket = new LcpPacket();
		synPacket.setSyn(state.getFile());
		state.completeAndSendPacket(synPacket);
		return SynSent.class;
	}

}
