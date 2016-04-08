package test.berryPicker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import berryPicker.FileObject;
import piTransfer.FileHelper;

public class FileObjectTest {
	private String fullFileName = "/Users/liesbeth.wijers/Desktop/input.txt";
	private String fileName = "input.txt";
	private FileObject fileObject;
	private File file;
	
	@Before
	public void setUp() {
		file = new File(fullFileName);
		fileObject = new FileObject(FileHelper.getFileContents(fullFileName), fileName);
	}
	
	@Test
	public void testSetUp() {
		assertNotNull(fileObject);
		assertEquals(fileName, fileObject.getName());
		assertTrue(fileObject.getContent().length > 0);
	}
	
	@Test
	public void testLengthAndParts() {
		int contentLength = fileObject.getContent().length;
		assertEquals(1, fileObject.numberOfParts(contentLength));
		double smallerPackets = contentLength/2.5;
		assertEquals(3, fileObject.numberOfParts((int) smallerPackets));
		
		// When the length is not specifically set, it is set to the default
		assertEquals(fileObject.DEFAULT_LENGTH, fileObject.getBytesPerPart());
		assertEquals(1, fileObject.numberOfParts());
		
		// Set the number of bytes per part
		fileObject.setBytesPerPart((int) smallerPackets);
		assertEquals(3, fileObject.numberOfParts());
		assertNotNull(fileObject.getPart(1));
	}
	
	@Test
	public void testSettingEmptyContent() {
		fileObject = new FileObject(null, "my_file.txt");
		assertNull(fileObject.getContent());
		
		int contentLength = 100;
		fileObject.setEmptyContent(contentLength);
		assertNotNull(fileObject.getContent());
		assertEquals(contentLength, fileObject.getContent().length);
	}
	
	@Test
	public void testWritingParts() {
		fileObject = new FileObject(null, "my_file.txt");
		byte[] part = "Hello, World!".getBytes();
		assertFalse(fileObject.setPart(part, 0));
		
		fileObject.setEmptyContent(part.length);
		fileObject.setBytesPerPart(part.length);
		assertEquals(part.length, fileObject.getContent().length);
		assertTrue(fileObject.setPart(part, 0));
		assertEquals(new String(fileObject.getContent()), new String(part));
	}
}
