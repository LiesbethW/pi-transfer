package lcp;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class LcpUtilities {
	public static String IP_RANGE = "172.17.2";

	public static InetAddress getInetAddress() {
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
	
}
