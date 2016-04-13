package piTransfer;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import berryPicker.Transmitter;

public class FileController implements FileStore {
	private Transmitter transmitter;
	
	public FileController(Transmitter transmitter) {
		this.transmitter = transmitter;
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
	
	/**
	 * For ease of testing
	 * @return
	 */
	public Transmitter getTransmitter() {
		return transmitter;
	}
}
