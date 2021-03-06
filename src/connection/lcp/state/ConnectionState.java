package connection.lcp.state;

import berryPicker.FileObject;
import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;

public interface ConnectionState {

	public abstract Class<? extends AbstractConnectionState> digest(LcpPacket lcpp);
	
	public abstract void completeAndSendPacket(LcpPacket lcpp);
	
	public abstract void startTransmission();
	
	public abstract boolean maxTimeoutsReached();
	
	public abstract boolean transmissionCompleted();
	
	public abstract boolean downloadCompleted();
	
	public abstract FileObject getFile();
	
	public LcpConnection getConnection();
}
