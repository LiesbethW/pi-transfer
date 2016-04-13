package piTransfer;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class FileController implements FileStore {
	
	public FileController() {
		
	}
	
	public void save(byte[] fileContents, String filename) {
		FileHelper.writeFileContents(fileContents, filename);
		System.out.format("Wrote the file %s to disk\n", filename);
	}
	
	public byte[] getContent(String pathname) throws FileNotFoundException {
		return FileHelper.getFileContents(pathname);
	}
	
	public String getFilename(String pathname) throws FileNotFoundException {
		return FileHelper.getFilename(pathname);
	}
	
	public ArrayList<String> listLocalFiles() {
		return FileHelper.getFileNames();
	}
}
