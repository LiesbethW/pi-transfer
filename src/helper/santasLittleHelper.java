package helper;

public class santasLittleHelper {
	
	public static void main(String[] args) {
		byte[] buffer = new byte[12];
		byte[] header = new byte[6];
		for (int i = 0; i < header.length; i++ ) {
			header[i] = (byte) i;
		}
		String hello = "Hello, World!";
		byte[] data = hello.getBytes();
		System.arraycopy(header, 0, buffer, 0, header.length);
		System.arraycopy(data, 0, buffer, header.length, data.length);
		for (int i = 0; i < buffer.length; i++) {
			System.out.println(buffer[i]);
		}
	}
}
