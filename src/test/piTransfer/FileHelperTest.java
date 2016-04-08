package test.piTransfer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Test;

import piTransfer.FileHelper;

public class FileHelperTest {
	private String input = "/Users/liesbeth.wijers/Desktop/input.txt";
	private String output = "output.txt";
	private byte[] fileContents;
	
	@Test
	public void testReadingAndWriting() {
		fileContents = FileHelper.getFileContents(input);
		assertNotNull(fileContents);
		File result = new File(FileHelper.FILE_DIR + File.separator + output);
		System.out.println(result.getAbsolutePath());
		assertFalse(result.length() > 0);
		FileHelper.writeFileContents(fileContents, output);
		assertTrue(result.length() > 0);
	}
	
	@Test
	public void testGetFileNames() {
		int dummyFilesAmount = 5;
		generateDummyFiles(dummyFilesAmount);
		ArrayList<String> fileNames = FileHelper.getFileNames();
		assertTrue(fileNames.size() >= dummyFilesAmount);
		assertTrue(fileNames.contains("dummy_file_0.txt"));
		System.out.println(fileNames.toString());
	}
	
	private void generateDummyFiles(int amount) {
		String[] fileNames = new String[amount];
		byte[] content = "Hello You!".getBytes();
		for (int i = 0; i < fileNames.length; i++) {
			fileNames[i] = String.format("dummy_file_%d.txt", i);
			FileHelper.writeFileContents(content, fileNames[i]);
		}
	}
	
	@After
	public void setUp() {
		File result = new File(FileHelper.FILE_DIR + File.separator + output);
		result.delete();
	}
	
}
