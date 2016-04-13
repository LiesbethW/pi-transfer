package test.lcp.strategies;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import berryPicker.FileObject;
import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;
import connection.lcp.strategies.SlidingWindowStrategy;

public class TestLcpConnection extends LcpConnection {
	private BlockingQueue<LcpPacket> sentPackets;
	
	public TestLcpConnection(String filename, int fileSize) {
		super(null, new FileObject(new byte[fileSize], filename), (short) new Random().nextInt(62000), null);
		sentPackets = new LinkedBlockingQueue<LcpPacket>();
		this.strategy = new SlidingWindowStrategy(this);
	}
	
	@Override
	public void completeAndSendPacket(LcpPacket lcpp) {
		sentPackets.offer(lcpp);		
	}
	
}
