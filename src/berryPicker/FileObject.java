package berryPicker;

import java.net.InetAddress;

public class FileObject {
	public static int DEFAULT_LENGTH = 1444;
	
	private String name;
	private byte[] content;
	private InetAddress destination;
	private int bytesPerPart = DEFAULT_LENGTH;
	
	public FileObject(byte[] content, String name) {
		setContent(content);
		setName(name);
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
	
	// setPart(int sequenceNumber, int bytesPerPart),
	// getChecksum(), setChecksum. encrypt(), decrypt(), compress(), decompress()
	
	public void setContent(byte[] content) {
		this.content = content;
	}
	
	public void setEmptyContent(int length) {
		this.content = new byte[length];
	}
	
	public boolean setPart(byte[] data, int sequenceNumber) {
		return setPart(data, sequenceNumber, getBytesPerPart());
	}
	
	public boolean setPart(byte[] data, int sequenceNumber, int bytesPerPart) {
		int offset = sequenceNumber*bytesPerPart;
		int ending = (sequenceNumber + 1)*bytesPerPart;
		if (content == null || ending > getLength()) {
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
	
}
