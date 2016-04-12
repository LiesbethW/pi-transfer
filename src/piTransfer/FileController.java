package piTransfer;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import berryPicker.BerryPicker;
import berryPicker.Transmitter;

public class FileController implements FileStore {
	private Transmitter transmitter;
	
	public FileController() {
		transmitter = new BerryPicker(this);
		Thread transmissionThread = new Thread(transmitter);
		transmissionThread.start();
	}
	
	public void upload(String pathName) throws FileNotFoundException {
		byte[] fileContent = FileHelper.getFileContents(pathName);
		String fileName = FileHelper.getFilename(pathName);
		transmitter.uploadFile(fileContent, fileName);
	}
	
	public void download(String filename) {
		transmitter.downloadFile(filename);
	}
	
	public ArrayList<String> listRemoteFiles() {
		ArrayList<String> files = new ArrayList<String>();
		files.addAll(transmitter.listRemoteFiles());
		return files;
	}
	
	public ArrayList<String> listDevices() {
		ArrayList<String> devices = new ArrayList<String>();
		for (Integer deviceId : transmitter.listDevices()) {
			devices.add(String.valueOf(deviceId));
		}
		return devices;
	}
	
	public void save(byte[] fileContents, String filename) {
		FileHelper.writeFileContents(fileContents, filename);
		System.out.format("Wrote the file %s to disk\n", filename);
	}
	
	public byte[] get(String filename) throws FileNotFoundException {
		return FileHelper.getFileContents(filename);
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
