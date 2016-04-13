package test.lcp.strategies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;
import connection.lcp.strategies.SlidingWindowStrategy;

public class SlidingWindowStrategyTest {
	private LcpConnection connection;
	private SlidingWindowStrategy strategy;
	private SlidingWindowStrategy receiving;
	
	@Before
	public void setUp() {
		connection = new TestLcpConnection("test.txt", 400*1500);
		strategy = new SlidingWindowStrategy(connection);
		
	}
	
	@Test
	public void testSetUp() {
		assertNotNull(connection);
		assertNotNull(strategy);
		assertEquals(strategy.DEFAULT_WINDOW_SIZE, strategy.slidingWindow().size());
	}
	
	@Test
	public void testTransmittingWindow() {
		assertEquals(strategy.DEFAULT_WINDOW_SIZE, strategy.slidingWindow().size());
		assertTrue(strategy.slidingWindow().containsKey((byte) 0));
		assertFalse(strategy.slidingWindow().containsKey((byte) strategy.DEFAULT_WINDOW_SIZE));
		
		byte sn = 0;
		strategy.sendPart(sn, 0);
		
		LcpPacket ack = new LcpPacket();
		ack.setFilePartAck(sn);
		
		strategy.handleAck(ack);
		assertFalse(strategy.slidingWindow().containsKey(sn));
		assertTrue(strategy.slidingWindow().containsKey((byte) strategy.DEFAULT_WINDOW_SIZE));
	}
	
	@Test
	public void testAckWindow() throws InterruptedException {
		receiving = new SlidingWindowStrategy(connection);
		for (int i = 0; i < 300; i++) {
			byte sn = (byte) i;
			strategy.sendPart(sn, i);
			
			LcpPacket filePart = new LcpPacket();
			filePart.setFilePart(new byte[100], sn);
			receiving.handleFilePart(filePart);
			
			LcpPacket ack = new LcpPacket();
			ack.setFilePartAck(sn);
			
			Thread.sleep(20);
			
			strategy.handleAck(ack);
			System.out.format("Got ack %d\n", (int) sn);
			assertFalse(strategy.slidingWindow().containsKey(sn));
			assertTrue(strategy.slidingWindow().containsKey((byte) (sn + strategy.DEFAULT_WINDOW_SIZE)));
		}
	}
	
}
