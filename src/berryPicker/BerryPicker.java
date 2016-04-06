package berryPicker;

import java.io.IOException;

import connection.ConnectionHandler;

public class BerryPicker implements Runnable {
	private ConnectionHandler broadcastChannel;
	
	public BerryPicker() {
		try {
			broadcastChannel = new ConnectionHandler();
			Thread thread = new Thread(broadcastChannel);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		
	}
}
