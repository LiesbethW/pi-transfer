package piTransfer;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public interface FileStore {

	// Interface towards transmitter layer
	public void save(byte[] fileContents, String fileName);
	
	public byte[] getContent(String pathname) throws FileNotFoundException;
	
	public String getFilename(String pathname) throws FileNotFoundException;
	
	public ArrayList<String> listLocalFiles();
	
}
