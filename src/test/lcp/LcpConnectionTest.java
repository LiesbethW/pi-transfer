package test.lcp;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import berryPicker.BerryPicker;
import berryPicker.FileObject;
import berryPicker.Transmitter;
import connection.ConnectionHandler;
import connection.Utilities;
import connection.lcp.LcpConnection;
import piTransfer.FileController;
import piTransfer.FileStore;


public class LcpConnectionTest {
	private FileStore store;
	private Transmitter transmitter;
	private ConnectionHandler handler;
	private LcpConnection connection;
	private FileObject file;
	private short vcid;
	
	@Before
	public void createConnection() throws IOException {
		store = new FileController();
		transmitter = ((FileController) store).getTransmitter();
		handler = ((BerryPicker) transmitter).getConnectionHandler();
		file = new FileObject("My file content.".getBytes(), "my_file.txt");
		file.setDestination(Utilities.getInetAddressEndingWith(2));
		vcid = 23987;
		connection = new LcpConnection(handler, file, vcid, null);
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
		handler.getClient().kill();
	}
	

}
