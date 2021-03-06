package connection.lcp.state;

import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;
import connection.lcp.Protocol;
import connection.lcp.commands.SendSyn;
import connection.lcp.commands.SendSynAck;

public class Initialized extends AbstractConnectionState {
	public static int START_FLAG = 0;
	
	public Initialized(LcpConnection connection) {
		super(connection);
	}
	
	public Class<? extends AbstractConnectionState> digest(LcpPacket lcpp) {
		return transition(lcpp);
	}
	
	@Override
	protected Class<? extends AbstractConnectionState> transition(LcpPacket lcpp) {
		if (lcpp == null) {
			return transitionMap.get(START_FLAG).runCommand(lcpp, this);
		} else {
			return super.transition(lcpp);
		}
	}

	@Override
	protected void initializeTransitionMap() {
		transitionMap.put(START_FLAG, new SendSyn());
		transitionMap.put(Protocol.FILE_REQUEST, new SendSyn());
		transitionMap.put(Protocol.SYN, new SendSynAck());
	}

}
