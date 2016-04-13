package berryPicker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import connection.ConnectionHandler;
import connection.Transmitter;
import connection.Utilities;
import piTransfer.FileController;
import piTransfer.FileStore;
import ui.InteractionController;

public class BerryPicker implements BerryHandler {
	private InteractionController controller;
	private FileStore store;
	private Transmitter connectionHandler;
	private BlockingQueue<FileObject> receivedFiles = new LinkedBlockingQueue<FileObject>();
	private ConcurrentHashMap<FileObject, Integer> filesToUpload = new ConcurrentHashMap<FileObject, Integer>();
	private ConcurrentHashMap<String, Double> filesToDownload = new ConcurrentHashMap<String, Double>();
	private HashMap<Integer, Date> berries = new HashMap<Integer, Date>();
	private ConcurrentHashMap<Integer, ArrayList<String>> availableFiles = new ConcurrentHashMap<Integer, ArrayList<String>>();
	
	public BerryPicker(InteractionController controller) {
		try {
			this.controller = controller;
			this.store = new FileController();
			connectionHandler = new ConnectionHandler(this);
			Thread thread = new Thread(connectionHandler);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * While running, see if there are received files to save.
	 */
	public void run() {
		while(true) {
			checkForFilesToSave();
		}
	}
	
	/**
	 * Check if there are complete received files to save. Remove
	 * those from the list of files to download if they were there.
	 */
	public void checkForFilesToSave() {
		FileObject receivedFile;
		try {
			receivedFile = receivedFiles.poll(1, TimeUnit.SECONDS);
			if (receivedFile != null) {
				store.save(receivedFile.getContent(), receivedFile.getName());
				availableFiles.put(Utilities.getMyId(), store.listLocalFiles());
				if (filesToDownload.containsKey(receivedFile.getName())) {
					filesToDownload.remove(receivedFile.getName());
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	
	public boolean upload(String pathname) {
		if (berries.size() < 1) {
			controller.displayError("There are no Raspberry Pies available to upload this file to.");
			return false;
		} else if (berries.size() > 1) {
			controller.displayError("There are multiple devices available. Please use \"PUT filename device\".");
			return false;
		} else {
			this.upload(pathname, this.berryLastHeardOf());
		}
		return true;
	}

	public boolean upload(String pathname, int berryId) {
		if (filesToUpload.containsKey(pathname) && filesToUpload.get(pathname).equals(berryId)) {
			controller.displayError("This file is already being uploaded to this berry.");
			return false;
		} else {
			try {
				this.uploadFile(store.getContent(pathname), store.getFilename(pathname), berryId);
			} catch (FileNotFoundException e) {
				controller.displayError(String.format("The file at path %s could not be found", pathname));
			}
			return true;
		}
	}
	
	/**
	 * Upload the file to the selected device
	 * @param fileContents
	 * @param fileName
	 * @param berryId
	 */
	private void uploadFile(byte[] fileContents, String fileName, int berryId) {
		FileObject file = new FileObject(fileContents, fileName);
		file.setDestination(getBerryById(berryId));
		this.filesToUpload.put(file, berryId);
		connectionHandler.transmitFile(file);
	}
	
	public byte[] downloadFile(String filename) {
		// TO DO
		return "The amazing content of your file.".getBytes();
	}
	
	public void saveFile(FileObject file) {
		receivedFiles.add(file);
	}
	
	/**
	 * Respond to a file request coming in from the network by
	 * starting to upload the file.
	 */
	public boolean getFile(String filename, int berryId) {
		if (this.listLocalFiles().contains(filename)) {
			try {
				this.uploadFile(store.getContent(filename), filename, berryId);
				return true;				
			} catch (FileNotFoundException e) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean download(String filename) {
		
		return false;
	}
	
	/**
	 * Use the incoming heartbeat to update the list of known Raspberry Pies
	 * and the files they claim to have.
	 */
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
	
	/**
	 * List the files that other Raspberry Pies have advertised that
	 * they have.
	 */
	public ArrayList<String> listRemoteFiles() {
		ArrayList<String> allFiles = new ArrayList<String>();
		for (Integer berryId : availableFiles.keySet()) {
			allFiles.addAll(availableFiles.get(berryId));
		}
		return allFiles;
	}
	
	/**
	 * List the files in the local file store.
	 */
	public ArrayList<String> listLocalFiles() {
		return store.listLocalFiles();
	}
	
	/**
	 * Return the devices that a heartbeat was received of.
	 */
	public ArrayList<String> listDevices() {
		ArrayList<Integer> ids = new ArrayList<Integer>(berries.keySet());
		ArrayList<String> devices = new ArrayList<String>();
		for (Integer deviceId : ids) {
			devices.add(String.valueOf(deviceId));
		}
		return devices;
	}
	
	/**
	 * For ease of testing
	 * @return
	 */
	public Transmitter getConnectionHandler() {
		return this.connectionHandler;
	}
	
	private InetAddress getBerryById(int berryId) {
		return connection.Utilities.getInetAddressEndingWith(berryId);
	}
	
	private int berryLastHeardOf() {
		int lastHeartbeatBerry = 255;
		Date lastDate = new Date(0);
		for (Integer berry : berries.keySet()) {
			if (berries.get(berry).after(lastDate)) {
				lastDate = berries.get(berry);
				lastHeartbeatBerry = berry;
			}
		}
		return lastHeartbeatBerry;
	}
	
	
}
