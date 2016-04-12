package piTransfer;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public interface FileStore {

	// Interface towards user interface
	public void upload(String filename) throws FileNotFoundException;
	
	public void download(String filename);
	
	public ArrayList<String> listRemoteFiles();
	
	public ArrayList<String> listDevices();
	
	// Interface towards transmitter layer
	public void save(byte[] fileContents, String fileName);
	
	public byte[] get(String fileName) throws FileNotFoundException;
	
	public ArrayList<String> listLocalFiles();
	
}
