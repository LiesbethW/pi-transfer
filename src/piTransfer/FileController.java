package piTransfer;

import java.io.File;
import java.util.ArrayList;

import berryPicker.BerryPicker;
import berryPicker.Transmitter;

public class FileController implements FileStore {
	
	public static void main(String[] args) {
		FileController piTransfer = new FileController();
		
		// Create a file for testing purposes
		String filename = String.format("example_file_%d.txt", (int) (Math.random()*10000));
		byte[] fileContents = "My super duper file content.".getBytes();
		piTransfer.save(fileContents, filename);
		String filePath = String.join(File.separator, FileHelper.FILE_DIR, filename);
		
		// Upload that file
		piTransfer.upload(filePath);
		
		// Clean up the file afterwards
		File file = new File(filePath);
		file.delete();
	}
	
	private Transmitter transmitter;
	
	public FileController() {
		transmitter = new BerryPicker(this);
	}
	
	public void upload(String filename) {
		byte[] fileContent = FileHelper.getFileContents(filename);
		transmitter.uploadFile(fileContent, filename);
	}
	
	public void download(String filename) {
		transmitter.downloadFile(filename);
	}
	
	public ArrayList<String> listFiles() {
		ArrayList<String> files = listLocalFiles();
		files.addAll(transmitter.listRemoteFiles());
		return files;
	}
	
	public void save(byte[] fileContents, String filename) {
		FileHelper.writeFileContents(fileContents, filename);
		System.out.format("Wrote the file %s to disk\n", filename);
	}
	
	public ArrayList<String> listLocalFiles() {
		return FileHelper.getFileNames();
	}
}
