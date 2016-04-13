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
			this.processFileRequest(lcpp);
		}
	}
	
	public void sendNewFileRequest(String filename, InetAddress berry) {
		LcpPacket lcpp = new LcpPacket();
		lcpp.setFileRequest(filename, encryption);
		lcpp.setDestination(berry, -1);
		lcpp.setSource();
		handler.send(lcpp);
	}
	
	private void processFileRequest(LcpPacket lcpp) {
		System.out.println("Processing file request");
		handler.berryHandler().getFile(lcpp.getFileName(), lcpp.getSourceId());
	}
	
	
}
