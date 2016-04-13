package ui;

import java.io.FileNotFoundException;
import java.util.HashMap;

import piTransfer.FileController;
import piTransfer.FileStore;

public class InteractionController implements Runnable {
	
	public static void main(String[] args) {
		InteractionController controller = new InteractionController();
		Thread thread = new Thread(controller);
		thread.start();
	}
	
	private FileStore fileStore;
	private View view;
	private UserListener userListener;
	private HashMap<String, Command> commands;
	
	public InteractionController() {
		fileStore = new FileController();
		view = new TextualInterface(this);
		userListener = new UserListener();
		initializeCommands();
	}
	
	public void run() {
		while (true) {
			processUserInput(UserListener.readString());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
			}
		}
	}
	
	public void processUserInput(String input) {
		String[] inputArguments = input.split(" ");
		String command = inputArguments[0].toUpperCase();
		String[] args = new String[inputArguments.length - 1];
		System.arraycopy(inputArguments, 1, args, 0, args.length);
		
		if (commands.containsKey(command)) {
			commands.get(command).runCommand(args);
		} else {
			System.out.format("The command %s is unknown.", command);
		}
	}
	
	public String[] getOptions() {
		String[] options = new String[0];
		return commands.keySet().toArray(options);
	}
	
	private void requireFilename() {
		view.showError("a file name is required");
	}
	
	private void initializeCommands() {
		commands = new HashMap<String, Command>();
		commands.put("PUT", new UploadFile());
		commands.put("GET", new DownloadFile());
		commands.put("LS", new ListRemoteFiles());
		commands.put("DEVICES", new ListDevices());
	}
	
	private interface Command {
		public void runCommand(String[] args);
	}
	
	private class UploadFile implements Command {
		public void runCommand(String[] args) {
			if (args.length < 1) {
				requireFilename();
			} else {
				try {
					fileStore.upload(args[0]);
				} catch(FileNotFoundException e) {
					view.showError(e.getMessage());
				}
			}
		}
	}
	
	private class DownloadFile implements Command {
		public void runCommand(String[] args) {
			if (args.length < 1) {
				requireFilename();
			} else {
				String filename = args[0];
				if (fileStore.listRemoteFiles().contains(filename)) {
					fileStore.download(filename);
				} else {
					view.showError(String.format("The file %s is not available on PiTransfer.", filename));
				}
				
			}
		}
	}
	
	private class ListRemoteFiles implements Command {
		public void runCommand(String[] args) {
			view.list(fileStore.listRemoteFiles());
		}
	}
	
	private class ListDevices implements Command {
		public void runCommand(String[] args) {
			view.list(fileStore.listDevices());
		}
	}
}