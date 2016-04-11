package piTransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileHelper {
	public static String FILE_DIR = "files";
	
	/**
	 * Gets the contents of the specified file.
	 * 
	 * @param id
	 *            the file ID
	 * @return the array of integers, representing the contents of the file to
	 *         transmit
	 */
	public static byte[] getFileContents(String filename) {
		File fileToTransmit = new File(filename);
		try (FileInputStream fileStream = new FileInputStream(fileToTransmit)) {
			byte[] fileContents = new byte[(int) fileToTransmit.length()];

			for (int i = 0; i < fileContents.length; i++) {
				byte nextByte = (byte) fileStream.read();
				if (nextByte == -1) {
					throw new Exception("File size is smaller than reported");
				}

				fileContents[i] = nextByte;
			}
			return fileContents;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println(e.getStackTrace());
			return null;
		}
	}
	
	/**
	 * Write byte array with the file contents to a new file with the specified
	 * filename. The written file is placed in the /files directory.
	 */
	public static void writeFileContents(byte[] fileContents, String filename) {
		String workingDirectory = System.getProperty("user.dir");
		// Be sure that the directory exists
		new File(FILE_DIR).mkdir();
		
		String filePath = workingDirectory + File.separator + FILE_DIR + File.separator + filename;

		File fileToSave = new File(filePath);
//		fileToSave.getAbsolutePath();
		
		try {
			if (fileToSave.createNewFile()) {
				try(FileOutputStream fileStream = new FileOutputStream(fileToSave)) {
					fileStream.write(fileContents);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("File already exists!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * List the files that are in the files directory.
	 */
	public static ArrayList<String> getFileNames() {
		ArrayList<String> fileNames = new ArrayList<String>();
		
		File folder = new File(FileHelper.FILE_DIR);
		
		// Be sure that the directory exists
		folder.mkdir();
		
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			fileNames.add(files[i].getName());
		}
		return fileNames;
	}
}
