package berryPicker;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;

public interface Transmitter extends Runnable {

	// Interface toward User Interface
	/**
	 * Request to upload a file
	 * @param pathname
	 * @return true if this task could be started, false if is is for example
	 * already being uploaded, or if a choice for the berry needs to be 
	 * made.
	 */
	public boolean upload(String pathname) throws FileNotFoundException;
	
	/**
	 * Request to upload a file
	 * @param pathname
	 * @param berryId
	 * @return false if the file is already being uploaded, the selected Rasberry Pi
	 * is not available or the file could not be found. True if the task was started
	 * @throws FileNotFoundException
	 */
	public boolean upload(String pathname, int berryId) throws FileNotFoundException;
	
	/**
	 * Starts the download if it 
	 * @param filename
	 */
	public boolean download(String filename);
	
	public ArrayList<String> listRemoteFiles();
	
	public ArrayList<String> listDevices();

	// Interface towards ConnectionHandler
	
	public void saveFile(FileObject file);
	
	public boolean getFile(String filename, int destinationId);

	public void processHeartbeat(int berryId, Date timestamp, ArrayList<String> files);
	
	public ArrayList<String> listLocalFiles();
	
}
