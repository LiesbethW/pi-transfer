package connection.lcp.strategies;

import berryPicker.FileObject;
import connection.lcp.LcpPacket;

public abstract class TransmissionStrategy {
	private FileObject file;
	
	public TransmissionStrategy() {
		
	}
	
	public abstract void accept(LcpPacket packet);
	
	public abstract void transmit(FileObject file);
	
}
