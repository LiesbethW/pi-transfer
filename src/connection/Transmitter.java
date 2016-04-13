package connection;

import java.net.InetAddress;

import berryPicker.FileObject;

public interface Transmitter extends Runnable {

	// Interface towards BerryHandler
	public void transmitFile(FileObject file);
	
	public void requestFile(String filename, InetAddress berry);
	
}
