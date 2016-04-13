package connection.lcp;

public interface LcpSender {

	// Interface towards LcpConnetion
	public void send(LcpPacket packet);
	
}
