package berryPicker;

import java.util.ArrayList;

public interface Transmitter {

	// Interface towards FileController
	public abstract void uploadFile(byte[] contents, String filename);
	
	public abstract byte[] downloadFile(String filename);
	
	public abstract ArrayList<String> listRemoteFiles();

	// Interface towards transmitter
	
	public abstract void saveFile(FileObject file);
	
	public abstract ArrayList<String> listFiles();
}
