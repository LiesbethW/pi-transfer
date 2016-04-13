package connection.lcp.strategies;

import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;

public class SlidingWindowStrategy extends TransmissionStrategy {
	public static int DEFAULT_WINDOW_SIZE = 10;
	private static Timer timer;
	
	private HashMap<Byte, Integer> sequenceNumberToFilePart;
	private HashMap<Byte, Boolean> acks;
	private int windowSize;
	private int seqNumToAck;
	private long estimatedRTT;
	private HashMap<Byte, Long> sendingTimes;

	public SlidingWindowStrategy(LcpConnection connection) {
		super(connection);
		windowSize = DEFAULT_WINDOW_SIZE;
		seqNumToAck = 0;
		estimatedRTT = 1000;
		sendingTimes = new HashMap<Byte, Long>();
		initializeSlidingWindow();
	}
	
	@Override
	public void startTransmission() {
		sendNextPart();
		Timer timer = new Timer();
		timer.schedule(new SendNextPart(this), (long) estimatedRTT/windowSize);
	}

	@Override
	public void handleFilePart(LcpPacket packet) {
		byte sequenceNumber = packet.getSequenceNumber();
		if (sequenceNumberToFilePart.containsKey(sequenceNumber)) {
			this.savePart(packet.getData(), sequenceNumberToFilePart.get(sequenceNumber));
			this.sendAck(sequenceNumber);
			acks.put(sequenceNumber, true);
			updateReceivingWindow(sequenceNumber);
		}
	}

	@Override
	public void handleAck(LcpPacket packet) {
		updateEstimatedRTT(packet.getSequenceNumber(), new Date());
		byte sequenceNumber = packet.getSequenceNumber();
		if (acks.containsKey(sequenceNumber)) {
			acks.put(sequenceNumber, true);
			if (sequenceNumberToFilePart.get(sequenceNumber) >= connection().getFile().lastPart()) {
				this.connection().setTransmissionCompleted();
			} else {
				updateTransmittingWindow(sequenceNumber);
//				sendNextPart();
			}
		}		
	}
	
	private void sendNextPart() {
		for (Byte sn : sequenceNumberToFilePart.keySet()) {
			if (!acks.get(sn)) {
				if (!sendingTimes.containsKey(sn)) {
					this.sendPart(sn, sequenceNumberToFilePart.get(sn));
				} else if (now() - sendingTimes.get(sn) > 2*estimatedRTT) {
					doubleEstimatedRTT();
					this.sendPart(sn, sequenceNumberToFilePart.get(sn));
				}
			}
		}
		timer = new Timer();
		timer.schedule(new SendNextPart(this), (long) estimatedRTT/windowSize); 
	}
	
	@Override
	public void sendPart(byte sequenceNumber, int partNumber) {
		super.sendPart(sequenceNumber, partNumber);
		sendingTimes.put(sequenceNumber, now());
	}

	public void updateTransmittingWindow(byte sequenceNumber) {
		byte sn = sequenceNumber;
		boolean acked = acks.get(sn);
		while (acked) {
			byte largestFrameToSend = (byte) (sn + windowSize);
			int largestPartToSend = sequenceNumberToFilePart.get(sn) + windowSize;
			sequenceNumberToFilePart.put(largestFrameToSend, largestPartToSend);
			acks.put(largestFrameToSend, false);
			
			sequenceNumberToFilePart.remove(sn);
			acks.remove(sn);
			
			sn++;
			acked = acks.get(sn);
		}
	}
	
	public void updateReceivingWindow(byte sequenceNumber) {
		byte sn = sequenceNumber;
		boolean acked = acks.get(sn);
		while (acked) {
			byte largestFrameToSend = (byte) (sn + windowSize);
			int largestPartToSend = sequenceNumberToFilePart.get(sn) + windowSize;
			sequenceNumberToFilePart.put(largestFrameToSend, largestPartToSend);
			acks.put(largestFrameToSend, false);
			
			sequenceNumberToFilePart.remove(sn);
			acks.remove(sn);
			
			sn++;
			acked = acks.get(sn);
		}
	}
	
	public void doubleEstimatedRTT() {
		estimatedRTT = 2*estimatedRTT;
	}
	
	public void updateEstimatedRTT(byte sequenceNumber, Date date) {
		long measuredRTT = date.getTime() - sendingTimes.get(sequenceNumber);
		estimatedRTT = (long) (0.6*measuredRTT + 0.4*estimatedRTT);
		System.out.format("Updated estimated RTT to %d\n", estimatedRTT);
	}
	
	private void initializeSlidingWindow() {
		sequenceNumberToFilePart = new HashMap<Byte, Integer>();
		acks = new HashMap<Byte, Boolean>();
		for (int i = 0; i < windowSize; i++) {
			sequenceNumberToFilePart.put((byte) i, i);
			acks.put((byte) i, false); 
		}
	}
	
	private long now() {
		return (new Date()).getTime();
	}
	
	/**
	 * For testing
	 */
	public HashMap<Byte, Integer> slidingWindow() {
		return this.sequenceNumberToFilePart;
	}
	
	public HashMap<Byte, Boolean> acks() {
		return this.acks;
	}
	
	private class SendNextPart extends TimerTask {
		private SlidingWindowStrategy strategy;
		
		public SendNextPart(SlidingWindowStrategy strategy) {
			this.strategy = strategy;
		}
		
		public void run() {
			strategy.sendNextPart();
		}
	}
}
