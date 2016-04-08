package connection.lcp;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import berryPicker.FileObject;
import connection.ConnectionHandler;
import connection.Utilities;
import connection.lcp.state.AbstractConnectionState;
import connection.lcp.state.Closed;
import connection.lcp.state.ConnectionState;
import connection.lcp.state.Established;
import connection.lcp.state.Initialized;
import connection.lcp.state.SynReceived;
import connection.lcp.state.SynSent;

public class LcpConnection implements Runnable {
	private static ArrayList<Class<? extends AbstractConnectionState> > stateList =
			new ArrayList<>(Arrays.asList(Initialized.class, SynSent.class, SynReceived.class,
					Established.class, Closed.class));
	
	// LCP Connection attributes
	private ConnectionHandler handler;
	private ConnectionState state;
	private InetAddress myIP;
	private InetAddress otherIP;
	private FileObject file;
	private short virtualCircuitID;
	
	// States
	private HashMap<Class<? extends AbstractConnectionState>, AbstractConnectionState> states;
	
	public LcpConnection(ConnectionHandler handler, FileObject file) {
		myIP = Utilities.getMyInetAddress();
		this.handler = handler;
		this.otherIP = file.getDestination();
		this.file = file;
		initializeStates();
		setState(Initialized.class);
	}
	
	public void run() {
		// DO SOMETHING
	}
	
	public void completeAndSendPacket(LcpPacket lcpp) {
		lcpp.setSource();
		lcpp.setDestination(otherIP, -1);
		lcpp.setVCID(virtualCircuitID);
		handler.send(lcpp);
	}
	
	public boolean isEstablished() {
		return Established.class.isInstance(getState());
	}
	
	public ConnectionState getState() {
		return state;
	}
	
	public void digest(LcpPacket lcpp) {
		setState(getState().digest(lcpp));
	}
	
	private void setState(Class<? extends ConnectionState> stateClass) {
		this.state = states.get(stateClass);
	}
	
	private void initializeStates() {
		states = new HashMap<Class<? extends AbstractConnectionState>, AbstractConnectionState>();
		for (int i = 0; i < stateList.size(); i++) {
			Class<? extends AbstractConnectionState> stateClass = stateList.get(i);
			try {
				AbstractConnectionState state = (AbstractConnectionState) (stateClass.getConstructors()[0]).newInstance(this, file);
				states.put(stateClass, state);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
