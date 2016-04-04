package berryPicker;

import java.io.IOException;

public class BerryPicker implements Runnable {
	private BroadcastBerry berry;
	
	public BerryPicker() {
		try {
			berry = new BroadcastBerry();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		
	}
}
