package piTransfer;

import berryPicker.BerryPicker;

public class Transferrer {
	
	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
		Transferrer piTransfer = new Transferrer();
	}
	
	private BerryPicker berryPicker;
	
	public Transferrer() {
		berryPicker = new BerryPicker();
	}
	
	public void upload(String filename) {
		byte[] fileContent = FileManager.getFileContents(filename);

	}
}
