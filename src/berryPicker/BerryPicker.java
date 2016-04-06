package berryPicker;

import java.io.IOException;

import connection.ConnectionHandler;

public class BerryPicker implements Runnable {
	private ConnectionHandler connectionHandler;
	
	public BerryPicker() {
		try {
			connectionHandler = new ConnectionHandler();
			Thread thread = new Thread(connectionHandler);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		
	}
}
