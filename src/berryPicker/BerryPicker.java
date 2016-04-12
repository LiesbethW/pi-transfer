package berryPicker;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import connection.ConnectionHandler;
import connection.Utilities;
import piTransfer.FileStore;

public class BerryPicker implements Transmitter {
	private FileStore store;
	private ConnectionHandler connectionHandler;
	private BlockingQueue<FileObject> receivedFiles = new LinkedBlockingQueue<FileObject>();
	private HashMap<Integer, Date> berries = new HashMap<Integer, Date>();
	private HashMap<Integer, ArrayList<String>> availableFiles = new HashMap<Integer, ArrayList<String>>();
	
	public BerryPicker(FileStore store) {
		try {
			this.store = store;
			availableFiles.put(Utilities.getMyId(), store.listLocalFiles());
			connectionHandler = new ConnectionHandler(this);
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
					availableFiles.put(Utilities.getMyId(), store.listLocalFiles());
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
	
	public void saveFile(FileObject file) {
		receivedFiles.add(file);
	}
	
	public boolean getFile(String filename, int berryId) {
		if (store.listFiles().contains(filename)) {
			this.uploadFile(store.get(filename), filename, berryId);
			return true;
		} else {
			return false;
		}
	}
	
	public void processHeartbeat(int berryId, Date timestamp, ArrayList<String> files) {
		if (berries.containsKey(berryId)) {
			if (berries.get(berryId).before(timestamp)) {
				berries.put(berryId, timestamp);
				availableFiles.put(berryId, files);
			}
		} else {
			berries.put(berryId, timestamp);
			availableFiles.put(berryId, files);
		}
	}
	
	public ArrayList<String> listRemoteFiles() {
		ArrayList<String> allFiles = new ArrayList<String>();
		for (Integer berryId : availableFiles.keySet()) {
			allFiles.addAll(availableFiles.get(berryId));
		}
		return allFiles;
	}
	
	public ArrayList<String> listLocalFiles() {
		return store.listLocalFiles();
	}
	
	/**
	 * For ease of testing
	 * @return
	 */
	public ConnectionHandler getConnectionHandler() {
		return this.connectionHandler;
	}
	
	private InetAddress getBerryById(int berryId) {
		return connection.Utilities.getInetAddressEndingWith(berryId);
	}
	
	
}
