package berryPicker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import connection.ConnectionHandler;
import connection.Transmitter;
import filemanaging.FileController;
import filemanaging.FileStore;
import ui.InteractionController;

public class BerryPicker implements BerryHandler {
	private InteractionController controller;
	private FileStore store;
	private Transmitter connectionHandler;
	private ConcurrentHashMap<FileStats, Integer> filesToUpload = new ConcurrentHashMap<FileStats, Integer>();
	private CopyOnWriteArrayList<FileStats> filesToDownload = new CopyOnWriteArrayList<FileStats>();
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
			checkForCompletedUploads();
			showProgression();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Check if there are complete received files to save. Remove
	 * those from the list of files to download.
	 */
	public void checkForFilesToSave() {
		FileStats readyStats = null;
		for (FileStats stats : filesToDownload) {
			if (stats.ready()) {
				FileObject file = stats.file();
				store.save(file.getContent(), file.getName());
				System.out.format("Download completed, saving file %s", file.getName());
				readyStats = stats;
				break;
			}
		}
		if (readyStats != null) {
			filesToDownload.remove(readyStats);
		}
	}
	
	/**
	 * Check if there are completely uploaded files. Remove
	 * those from the list of files to upload.
	 */
	public void checkForCompletedUploads() {
		FileStats readyStats = null;
		for (FileStats stats : filesToUpload.keySet()) {
			if (stats.ready()) {
				FileObject file = stats.file();
				System.out.format("Upload of %s to %d completed", file.getName(), filesToUpload.get(stats));
				readyStats = stats;
				break;
			}
		}
		if (readyStats != null) {
			filesToUpload.remove(readyStats);
		}
	}
	
	public void showProgression() {
		for (FileStats stats : filesToUpload.keySet()) {
			controller.showStats(stats.filename(), stats.fraction(), stats.speed());
		}
		for (FileStats stats : filesToDownload) {
			controller.showStats(stats.filename(), stats.fraction(), stats.speed());
		}
	}
	
	public void removeConnection(FileStats stats) {
		if (filesToDownload.contains(stats)) {
			if (stats.ready()) {
				FileObject file = stats.file();
				System.out.format("Download of %s is completed", file.getName());
				filesToDownload.remove(stats);
			} else {
				System.out.format("The download of %s was not finished", stats.filename());
			}
			
		} else if (filesToUpload.containsKey(stats)) {
			FileObject file = stats.file();
			System.out.format("Upload of %s to %d completed", file.getName(), filesToUpload.get(stats));
			filesToUpload.remove(stats);
		} else {
			System.err.println("The removing of stats in this way is not working.");
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
		connectionHandler.transmitFile(file);
	}
	
	/**
	 * Download the file if it is available from exactly one provider.
	 * @return False if the file is not available on one of the remote
	 * sources, or if it is ambiguous which file to download.
	 */
	public boolean download(String filename) {
		if (!this.listRemoteFiles().contains(filename)) {
			controller.displayError("This file is not available.");
			return false;
		} else {
			int berryId = -1;;
			for (Integer berry : availableFiles.keySet()) {
				if (availableFiles.get(berry).contains(filename)) {
					if (berryId == -1) {
						berryId = berry;
					} else {
						controller.displayError("There are multiple berries that"
								+ "have a file with this name.");
						return false;
					}
				}
			}
			return this.download(filename, berryId);
		}
		
	}
	
	/**
	 * Download the file from the given device
	 * @param filename
	 * @param berryId
	 * @return false if the file is not available on that device
	 */
	public boolean download(String filename, int berryId) {
		if (availableFiles.get(berryId).contains(filename)) {
			connectionHandler.requestFile(filename, this.getBerryById(berryId));
			return true;
		} else {
			return false;
		}
	}
	
	public void addToDownloadingList(FileStats stats) {
		filesToDownload.add(stats);
		System.out.format("Added %s to files to Download\n", stats.filename());
	}
	
	@Override
	public void addToUploadingList(FileStats stats, Integer berry) {
		filesToUpload.put(stats, berry);
		System.out.format("Added %s to files to Upload\n", stats.filename());
	}
	
	/**
	 * Respond to a file request coming in from the network by
	 * starting to upload the file.
	 */
	public boolean getFile(String filename, int berryId) {
		if (this.listLocalFiles().contains(filename)) {
			try {
				this.uploadFile(store.getPiTransferFile(filename), filename, berryId);
				return true;				
			} catch (FileNotFoundException e) {
				System.out.println("File not found!");
				return false;
			}
		} else {
			return false;
		}
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
