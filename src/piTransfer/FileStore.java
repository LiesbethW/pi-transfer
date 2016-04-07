package piTransfer;

public interface FileStore {

	public abstract void save(byte[] fileContents, String fileName);
	
}
