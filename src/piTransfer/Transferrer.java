package piTransfer;

import berryPicker.BerryPicker;
import berryPicker.Transmitter;

public class Transferrer implements FileStore {
	
	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
		Transferrer piTransfer = new Transferrer();
	}
	
	private Transmitter berryPicker;
	
	public Transferrer() {
		berryPicker = new BerryPicker(this);
	}
	
	public void upload(String filename) {
		byte[] fileContent = FileManager.getFileContents(filename);
		berryPicker.uploadFile(fileContent, filename);
	}
	
	public void download(String filename) {
		berryPicker.downloadFile(filename);
	}
	
	public void save(byte[] fileContents, String filename) {
		FileManager.writeFileContents(fileContents, filename);
	}
}
