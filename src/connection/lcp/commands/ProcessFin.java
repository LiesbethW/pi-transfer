package connection.lcp.commands;

import connection.lcp.LcpPacket;
import connection.lcp.state.AbstractConnectionState;
import connection.lcp.state.Closed;
import connection.lcp.state.ConnectionState;
import connection.lcp.state.Initialized;

public class ProcessFin implements Command {

	@Override
	public Class<? extends AbstractConnectionState> runCommand(LcpPacket lcpp, ConnectionState state) {
		
		LcpPacket finAck = new LcpPacket();
		finAck.setFinAck();
		state.completeAndSendPacket(finAck);
		
		if (state.downloadCompleted()) {
			
			return Closed.class;
		} else {
			
			System.out.format("The checksum did not check out. The length"
					+ " of file %s should be %d, the content I currently"
					+ " have is: \n%s\n", state.getFile().getName(),
					state.getFile().getLength(),
					new String(state.getFile().getContent()));
			
			return Initialized.class;
		}

	}

}
