package berryPicker;

import java.net.InetAddress;
import java.util.zip.CRC32;

public class FileObject {
	public static int DEFAULT_LENGTH = 1443;
	private static CRC32 checksumCalculator = new CRC32();
	
	private String name;
	private byte[] content;
	private InetAddress destination;
	private int bytesPerPart;
	private long checksum;
	
	public FileObject() {
		this(null, null);
		this.setEmptyContent(0);
	}
	
	public FileObject(byte[] content, String name) {
		setContent(content);
		setName(name);
		setBytesPerPart(DEFAULT_LENGTH);
	}
	
	public byte[] getContent() {
		return content;
	}
	
	public String getName() {
		return name;
	}
	
	public InetAddress getDestination() {
		return destination;
	}
	
	public int getLength() {
		if (content != null) {
			return content.length;
		} else {
			return 0;
		}
	}
	
	public int getBytesPerPart() {
		return bytesPerPart;
	}
	
	public byte[] getPart(int sequenceNumber) {
		if (getBytesPerPart() != 0) {
			return this.getPart(sequenceNumber, getBytesPerPart());
		} else {
			System.err.println("The number of bytes per part is not set");
			return null;
		}
	}
	
	public byte[] getPart(int sequenceNumber, int bytesPerPart) {
		byte[] part;
		int offset = sequenceNumber*bytesPerPart;
		if ( offset < getLength() ) {
			if ( (sequenceNumber + 1)*bytesPerPart < getLength() ) {
				part = new byte[bytesPerPart];
				System.arraycopy(getContent(), offset, part, 0, bytesPerPart);
			} else {
				int remainder = getLength() - offset;
				part = new byte[remainder];
				System.arraycopy(getContent(), offset, part, 0, remainder);
			}		
		} else {
			System.err.println("You're trying to access a part of the content that does not exist");
			part = null;
		}
		return part;
	}
	
	public int numberOfParts() {
		if (getBytesPerPart() != 0) {
			return this.numberOfParts(getBytesPerPart());
		} else {
			System.err.println("The number of bytes per part is not set");
			return 0;
		}
	}
	
	public int numberOfParts(int bytesPerPart) {
		return (int) Math.ceil((double) getLength()/bytesPerPart);
	}
	
	public int lastPart() {
		return numberOfParts() - 1;
	}
	
	// getChecksum(), setChecksum. encrypt(), decrypt(), compress(), decompress()
	
	public void setContent(byte[] content) {
		this.content = content;
	}
	
	public void setEmptyContent(int length) {
		this.content = new byte[length];
	}
	
	public boolean setPart(byte[] data, int partNumber) {
		return setPart(data, partNumber, getBytesPerPart());
	}
	
	public boolean setPart(byte[] data, int partNumber, int bytesPerPart) {
		int offset = partNumber*bytesPerPart;
		int ending = offset + data.length;
		if (content == null || ending > getLength()) {
			System.out.println("Cannot write data into file content: it's too big!");
			return false;
		} else {
			System.arraycopy(data, 0, content, offset, data.length);
			return true;
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDestination(InetAddress destination) {
		this.destination = destination;
	}
	
	public void setBytesPerPart(int bytesPerPart) {
		this.bytesPerPart = bytesPerPart;
	}
	
	public long getFileChecksum() {
		checksumCalculator.reset();
		checksumCalculator.update(this.getContent());
		return checksumCalculator.getValue();
	}
	
	public void setFileChecksum(long checksum) {
		this.checksum = checksum;
	}
	
	public boolean checkFileChecksum() {
		if (checksum == 0) {
			System.out.println("Checksum was never set.");
			return false;
		} else {
			long calculatedChecksum = this.getFileChecksum();
			
			System.out.format("Checksum was %d, calculated checksum is %d.\n",
					checksum, calculatedChecksum);
			return calculatedChecksum == checksum;
		}
		
	}
	
}
