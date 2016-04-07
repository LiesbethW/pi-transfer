package berryPicker;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import connection.ConnectionHandler;
import piTransfer.FileStore;

public class BerryPicker implements Runnable, Transmitter {
	private FileStore store;
	private ConnectionHandler connectionHandler;
	private BlockingQueue<FileObject> receivedFiles = new LinkedBlockingQueue<FileObject>();
	
	public BerryPicker(FileStore store) {
		try {
			this.store = store;
			connectionHandler = new ConnectionHandler();
			Thread thread = new Thread(connectionHandler);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		while(true) {
			FileObject receivedFile;
			try {
				receivedFile = receivedFiles.poll(1, TimeUnit.SECONDS);
				if (receivedFile != null) {
					store.save(receivedFile.getContent(), receivedFile.getName());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void uploadFile(byte[] fileContents, String fileName, int berryId) {
		FileObject file = new FileObject(fileContents, fileName);
		file.setDestination(getBerryById(berryId));
		connectionHandler.transmitFile(file);
	}
	
	public void uploadFile(byte[] fileContents, String fileName) {
		int berryId = 2;
		this.uploadFile(fileContents, fileName, berryId);
	}
	
	public byte[] downloadFile(String filename) {
		// TO DO
		return "The amazing content of your file.".getBytes();
	}
	
	private InetAddress getBerryById(int berryId) {
		return connection.Utilities.getInetAddressEndingWith(berryId);
	}
	
	
}
