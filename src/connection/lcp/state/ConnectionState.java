package connection.lcp.state;

import berryPicker.FileObject;
import connection.lcp.LcpPacket;

public interface ConnectionState {

	public abstract Class<? extends AbstractConnectionState> digest(LcpPacket lcpp);
	
	public abstract void completeAndSendPacket(LcpPacket lcpp);
	
	public abstract boolean maxTimeoutsReached();
	
	public abstract FileObject getFile();
	
}
