package connection.lcp;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import berryPicker.FileObject;
import connection.Utilities;
import connection.lcp.state.AbstractConnectionState;
import connection.lcp.state.Closed;
import connection.lcp.state.ConnectionState;
import connection.lcp.state.Established;
import connection.lcp.state.FinSent;
import connection.lcp.state.Initialized;
import connection.lcp.state.SynReceived;
import connection.lcp.state.SynSent;
import connection.lcp.strategies.SlidingWindowStrategy;
import connection.lcp.strategies.TransmissionStrategy;

public class LcpConnection implements Runnable {
	private static ArrayList<Class<? extends AbstractConnectionState> > stateList =
			new ArrayList<>(Arrays.asList(Initialized.class, SynSent.class, SynReceived.class,
					Established.class, Closed.class, FinSent.class));
	
	// LCP Connection attributes
	private LcpSender handler;
	private ConnectionState state;
	protected TransmissionStrategy strategy;
	private InetAddress myIP;
	private InetAddress otherIP;
	private FileObject file;
	private short virtualCircuitID;
	protected boolean transmissionCompleted;
	
	// States
	private HashMap<Class<? extends AbstractConnectionState>, AbstractConnectionState> states;
	
	public LcpConnection(LcpSender handler, FileObject file, short virtualCircuitID, InetAddress address) {
		myIP = Utilities.getMyInetAddress();
		this.handler = handler;
		if (file != null) {
			this.file = file;
			this.setReceiver(address);
		} else {
			this.file = new FileObject();
			this.setSender(address);
		}
		this.virtualCircuitID = virtualCircuitID;
		this.strategy = new SlidingWindowStrategy(this);
		initializeStates();
		setState(Initialized.class);
	}
	
	public void run() {
		// DO SOMETHING
	}
	
	public void start() {
		digest(null);
	}
	
	public void completeAndSendPacket(LcpPacket lcpp) {
		lcpp.setSource();
		lcpp.setDestination(otherIP, -1);
		lcpp.setVCID(virtualCircuitID);
		handler.send(lcpp);
	}
	
	public TransmissionStrategy getStrategy() {
		return strategy;
	}
	
	public boolean isInitialized() {
		return Initialized.class.isInstance(getState());
	}
	
	public boolean isSynSent() {
		return SynSent.class.isInstance(getState());
	}
	
	public boolean isEstablished() {
		return Established.class.isInstance(getState());
	}
	
	public boolean isClosed() {
		return Closed.class.isInstance(getState());
	}
	
	public ConnectionState getState() {
		return state;
	}
	
	public void digest(LcpPacket lcpp) {
		setState(getState().digest(lcpp));
	}
	
	public boolean downloadCompleted() {
		return getFile().checkFileChecksum();
	}
	
	public boolean transmissionCompleted() {
		return transmissionCompleted;
	}
	
	public void setTransmissionCompleted() {
		transmissionCompleted = true;
	}
	
	public FileObject getFile() {
		return this.file;
	}
	
	public InetAddress getReceiver() {
		return otherIP;
	}
	
	public InetAddress getSender() {
		return otherIP;
	}
 	
	/**
	 * Set the file: has side effect of setting destination too!
	 * @param file
	 */
	public void setFile(FileObject file) {
		this.file = file;
		setSender(file.getDestination());
	}
	
	public void setSender(InetAddress sender) {
		this.otherIP = sender;
	}
	
	public void setReceiver(InetAddress receiver) {
		this.otherIP = receiver;
	}
	
	private void setState(Class<? extends ConnectionState> stateClass) {
		this.state = states.get(stateClass);
	}
	
	private void initializeStates() {
		states = new HashMap<Class<? extends AbstractConnectionState>, AbstractConnectionState>();
		for (int i = 0; i < stateList.size(); i++) {
			Class<? extends AbstractConnectionState> stateClass = stateList.get(i);
			try {
				AbstractConnectionState state = (AbstractConnectionState) (stateClass.getConstructors()[0]).newInstance(this);
				states.put(stateClass, state);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
