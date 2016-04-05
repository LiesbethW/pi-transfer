package test.lcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import org.junit.Before;
import org.junit.Test;

import lcp.LcpPacket;

public class LcpPacketTest {
	public static String IP = "172.17.2.1";
	public static int PORT = 3000;
	
	private LcpPacket packet;
	private InetAddress destination;
	private int port;
	
	@Before
	public void setUp() throws IOException {
		destination = InetAddress.getByName(IP);
		port = PORT;
		packet = new LcpPacket(destination, port);
	}
	
	@Test
	public void testSetUp() {
		assertNotNull(packet);
		assertEquals(destination, packet.getDestination());
		assertEquals(port, packet.getPort());
	}
	
	@Test
	public void testDatagram() {
		DatagramPacket datagram = packet.datagram();
		assertEquals(packet.getDestination(), datagram.getAddress());
		assertEquals(packet.getPort(), datagram.getPort());
	}
	
	@Test
	public void whatGoesInComesOut() {
		String message = "Hello, World!";
		packet.setMessage(message);
		assertEquals(message, packet.getMessage());
		DatagramPacket datagram = packet.datagram();
		assertTrue((new String(datagram.getData())).endsWith(message));
	}
	
	@Test
	public void testHeartbeat() {
		LcpPacket heartbeat = LcpPacket.heartbeat();
		assertEquals("I'm alive!", heartbeat.getMessage());
	}
	
}
