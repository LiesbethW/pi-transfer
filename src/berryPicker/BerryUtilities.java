package berryPicker;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class BerryUtilities {
	private static int BCAST_PORT = 1929;
	public static String BCAST_IP = "172.17.2.255";
	
	public static int getBroadcastPort() {
		return BCAST_PORT;
	}

	public static InetAddress broadcastAddress() {
		try {
			return InetAddress.getByName(BCAST_IP);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
