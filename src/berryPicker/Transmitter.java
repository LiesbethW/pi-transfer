package berryPicker;

public interface Transmitter {

	public abstract void uploadFile(byte[] contents, String filename);
	
	public abstract byte[] downloadFile(String filename);
	
}
