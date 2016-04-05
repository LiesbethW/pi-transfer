package berryPicker;

import java.io.IOException;

import overheadConnection.BroadcastConnection;

public class BerryPicker implements Runnable {
	private BroadcastConnection broadcastChannel;
	
	public BerryPicker() {
		try {
			broadcastChannel = new BroadcastConnection();
			Thread thread = new Thread(broadcastChannel);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		
	}
}
