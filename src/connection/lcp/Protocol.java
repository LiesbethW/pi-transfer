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
	public static int SYN = 1;
	public static int ACK = 2;
	public static int SYN_ACK = 4;
	public static int FIN = 8;
	public static int FIN_ACK = 10;
	public static int FILE_PART = 16;
	public static int FILE_REQUEST = 32;
	public static int NEGOTIATION = 64;
	public static int HEARTBEAT = 255;
	
	// delimiters
	public static String DELIMITER = "\n";
	public static String DELIMITER2 = ":";
	public static String DELIMITER3 = ",";
	
	// option strings
	public static String FILENAME = "FILENAME";
	public static String TOTAL_LENGTH = "TOTAL_LENGTH";
	public static String FILE_CHECKSUM = "FILE_CHECKSUM";
	public static String WINDOWSIZE = "WINDOWSIZE";
	public static String ENCRYPTION = "ENCRYPTION";
	public static String OFFSET = "OFFSET";
	public static String TIMESTAMP = "TIMESTAMP";
	public static String FILES = "FILES";
	
}
