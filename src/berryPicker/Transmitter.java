package berryPicker;

import java.util.ArrayList;
import java.util.Date;

public interface Transmitter extends Runnable {

	// Interface towards FileController
	public abstract void uploadFile(byte[] contents, String filename);
	
	public abstract byte[] downloadFile(String filename);
	
	public abstract ArrayList<String> listRemoteFiles();

	// Interface towards ConnectionHandler
	
	public abstract void saveFile(FileObject file);
	
	public abstract boolean getFile(String filename, int destinationId);

	public abstract void processHeartbeat(int berryId, Date timestamp, ArrayList<String> files);
	
	public abstract ArrayList<String> listLocalFiles();
	
}
