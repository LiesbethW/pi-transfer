package connection;

import java.net.InetAddress;

import connection.lcp.LcpPacket;

public class GeneralCommunicator {
	private ConnectionHandler handler;
	private boolean encryption;
	
	public GeneralCommunicator(ConnectionHandler handler) {
		this.handler = handler;
		this.encryption = false;
	}
	
	public void process(LcpPacket lcpp) {
		if (lcpp.fileRequest()) {

		}
	}
	
	public void sendFileRequest(String filename, InetAddress berry) {
		int offset = 0;
		sendFileRequest(filename, offset, berry);
	}
	
	public void sendFileRequest(String filename, int offset, InetAddress berry) {
		LcpPacket lcpp = new LcpPacket();
		lcpp.setFileRequest(filename, offset, encryption);
		handler.send(lcpp);
	}
	
	
	
}
