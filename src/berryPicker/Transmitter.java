package berryPicker;

import java.util.ArrayList;
import java.util.Date;

public interface Transmitter extends Runnable {

	// Interface towards FileController
	public void uploadFile(byte[] contents, String filename);
	
	public byte[] downloadFile(String filename);
	
	public ArrayList<String> listRemoteFiles();
	
	public ArrayList<Integer> listDevices();

	// Interface towards ConnectionHandler
	
	public void saveFile(FileObject file);
	
	public boolean getFile(String filename, int destinationId);

	public void processHeartbeat(int berryId, Date timestamp, ArrayList<String> files);
	
	public ArrayList<String> listLocalFiles();
	
}
