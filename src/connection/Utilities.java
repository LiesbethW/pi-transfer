package connection;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class Utilities {
	private static int BCAST_PORT = 1929;
	public static String BCAST_IP = "172.17.2.255";
	public static String IP_RANGE = "172.17.2";

	public static InetAddress getMyInetAddress() {
		InetAddress myAddress = null;
		try {
			Enumeration e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements())
			{
			    NetworkInterface n = (NetworkInterface) e.nextElement();
			    Enumeration ee = n.getInetAddresses();
			    while (ee.hasMoreElements())
			    {
			        InetAddress i = (InetAddress) ee.nextElement();
			        if (i.getHostAddress().startsWith(IP_RANGE)) {
			        	myAddress = i;
			        	break;
			        }
			    }
			}
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return myAddress;
	}
	
	public static int getMyId() {
		return (int) 0xff & getMyInetAddress().getAddress()[3];
	}
	
	public static InetAddress getInetAddressEndingWith(int id) {
		String host = String.join(".", IP_RANGE, String.valueOf(id));
		try {
			return InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			return null;
		}
	}
	
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
