package piTransfer;

import java.util.ArrayList;

public interface FileStore {

	// Interface towards user interface
	public abstract void upload(String filename);
	
	public abstract void download(String filename);
	
	public abstract ArrayList<String> listFiles();
	
	// Interface towards transmitter layer
	public abstract void save(byte[] fileContents, String fileName);
	
	public abstract byte[] get(String fileName);
	
	public abstract ArrayList<String> listLocalFiles();
	
}
