package test.lcp;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import berryPicker.BerryHandler;
import berryPicker.BerryPicker;
import berryPicker.FileObject;
import connection.ConnectionHandler;
import connection.Transmitter;
import connection.Utilities;
import connection.lcp.LcpConnection;
import connection.lcp.LcpSender;


public class LcpConnectionTest {
	private BerryHandler berryHandler;
	private Transmitter handler;
	private LcpConnection connection;
	private FileObject file;
	private short vcid;
	
	@Before
	public void createConnection() throws IOException {
		berryHandler = new BerryPicker(null);
		handler = ((BerryPicker) berryHandler).getConnectionHandler();
		file = new FileObject("My file content.".getBytes(), "my_file.txt");
		file.setDestination(Utilities.getInetAddressEndingWith(2));
		vcid = 23987;
		connection = new LcpConnection((LcpSender) handler, file, vcid, null);
	}
	
	@Test
	public void testInitialState() {
		assertTrue(connection.isInitialized());
	}
	
	@Test
	public void testStateTransition() {
		connection.digest(null);
		System.out.println(connection.getState());
		assertTrue(connection.isSynSent());
	}
	
	@After
	public void closeSocket() throws InterruptedException {
		Thread.sleep(500);
		((ConnectionHandler) handler).getClient().kill();
	}
	

}
