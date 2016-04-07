package test.lcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import connection.lcp.LcpPacket;

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
		assertEquals(destination, packet.getAddress());
		assertEquals(port, packet.getPort());
	}
	
	@Test
	public void testDatagram() {
		DatagramPacket datagram = packet.datagram();
		assertEquals(packet.getAddress(), datagram.getAddress());
		assertEquals(packet.getPort(), datagram.getPort());
	}
	
	@Test
	public void whatGoesInComesOut() {
		String message = "Hello, World!";
		packet.setMessage(message);
		assertEquals(message, packet.getMessage());
		// Turn packet into datagram
		DatagramPacket datagram = packet.datagram();
		
		// Turn datagram into packet
		LcpPacket newPacket = new LcpPacket(datagram);
		assertEquals(message, newPacket.getMessage());
	}
	
	@Test
	public void testChecksum() {
		packet.setMessage("Hello, World!");
		// Turen packet into datagram: this includes adding checksum
		DatagramPacket datagram = packet.datagram();
		// Turn datagram into packet
		LcpPacket newPacket = new LcpPacket(datagram);
		assertTrue(newPacket.checkChecksum());
	}
	
	@Test
	public void testHeartbeat() {
		LcpPacket heartbeat = LcpPacket.heartbeat();
		assertEquals("I'm alive!", heartbeat.getMessage());
	}
	
	@Test
	public void testSettingSynFlag() {
		assertFalse(packet.syn());
		packet.setSyn();
		assertTrue(packet.syn());
	}
	
	@Test
	public void testSourceSetting() throws UnknownHostException {
		assertEquals(InetAddress.getByName("172.17.2.0"), packet.getSource());
		packet.setSource();
		assertEquals(InetAddress.getByName("172.17.2.12"), packet.getSource());
	}
	
}
