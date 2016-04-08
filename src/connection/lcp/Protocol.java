package connection.lcp;

public interface Protocol {
	public static int HEADERLEN = 15;
	public static int VERSION = 1;
	public static int DEFAULTPORT = 1929;
	
	// Headerfields
	public static int VERSION_FIELD = 0;
	public static int FLAG_FIELD = 1;
	public static int SOURCE_FIELD = 2;
	public static int DESTINATION_FIELD = 3;
	public static int VCID_FIELD = 4;
	public static int PACKET_CHECKSUM_FIELD = 6;
	public static int SEQUENCE_NUMBER = 14;
	
	// FLAGS
	public static byte SYN = 1;
	public static byte ACK = 2;
	public static byte SYN_ACK = 4;
	public static byte FIN = 8;
	public static byte FIN_ACK = 10;
	public static byte FILE_PART = 16;
	public static byte FILE_REQUEST = 32;
	public static byte NEGOTIATION = 64;
	public static byte HEARTBEAT = (byte) 255;
	
}
