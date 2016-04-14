package connection.lcp.strategies;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import connection.lcp.LcpConnection;
import connection.lcp.LcpPacket;

public class SlidingWindowStrategy extends TransmissionStrategy {
	public static int DEFAULT_WINDOW_SIZE = 10;
	private static Timer timer;
	
	private ConcurrentHashMap<Byte, Integer> sequenceNumberToFilePart;
	private ConcurrentHashMap<Byte, Boolean> acks;
	private int windowSize;
	private byte lastReceivedAck;
	private byte seqNumToAck;
	private long estimatedRTT;
	private ConcurrentHashMap<Byte, Long> sendingTimes;

	public SlidingWindowStrategy(LcpConnection connection) {
		super(connection);
		windowSize = DEFAULT_WINDOW_SIZE;
		lastReceivedAck = 0;
		seqNumToAck = 0;
		estimatedRTT = 10000;
		sendingTimes = new ConcurrentHashMap<Byte, Long>();
		initializeSlidingWindow();
	}
	
	@Override
	public void startTransmission() {
		sendNextPart();
		timer = new Timer();
		timer.schedule(new SendNextPart(this), (long) 2*estimatedRTT);
	}

	@Override
	public void handleFilePart(LcpPacket packet) {
		byte sequenceNumber = packet.getSequenceNumber();
		System.out.format("Received packet %d\n", sequenceNumber);
		if (sequenceNumberToFilePart.containsKey((Byte) sequenceNumber)) {
			this.savePart(packet.getData(), sequenceNumberToFilePart.get((Byte) sequenceNumber).intValue());
			acks.put(sequenceNumber, true);
			if (sequenceNumberToFilePart.get(sequenceNumber).equals(this.connection().getFile().lastPart())) {
				System.out.println("The download has completed.");
			}
			updateReceivingWindow();
		}
		this.sendAck((byte) (seqNumToAck - 1));
	}

	@Override
	public void handleAck(LcpPacket packet) {
		// Only cancel and schedule a new timer if another sequence
		// number has arrived than the seqNumToAck - 1.
		if (! ((packet.getSequenceNumber()^(seqNumToAck-1)) == 0)) {
			timer.cancel();
			timer = new Timer();
			timer.schedule(new SendNextPart(this), (long) 2*estimatedRTT); 	
		} else {
			System.out.println("That is the same ack again: let that timer run off!");
		}

		connection().resetTimeOuts();
		
		updateEstimatedRTT(packet.getSequenceNumber(), new Date());
		byte sequenceNumber = packet.getSequenceNumber();
		System.out.format("Received ack %d\n", sequenceNumber);
		byte b = seqNumToAck;
		while (b != (byte) (sequenceNumber + 1)) {
			if (acks.containsKey(b)) {
				acks.put(b, true);
				System.out.format("Set %d true\n", b);
			}
			b++;
		}
		if (sequenceNumberToFilePart.containsKey(sequenceNumber) && 
				sequenceNumberToFilePart.get(sequenceNumber) >= connection().getFile().lastPart()) {
			this.connection().setTransmissionCompleted();
			System.out.println("The upload has completed.");
		}
		updateTransmittingWindow();
		
		sendNextPart();	
	}
	
	private void sendNextPart() {
		for (Byte sn : sequenceNumberToFilePart.keySet()) {
			if (!acks.get(sn)) {
				if (!sendingTimes.containsKey(sn)) {
					this.sendPart(sn, sequenceNumberToFilePart.get(sn));
				} else if (now() - sendingTimes.get(sn) > 2*estimatedRTT) {
					this.sendPart(sn, sequenceNumberToFilePart.get(sn));
				}
			}
		}
	}
	
	private void sendAllFramesAgain() {
		for (Byte sn : sequenceNumberToFilePart.keySet()) {
			if (!acks.get(sn)) {
				this.sendPart(sn, sequenceNumberToFilePart.get(sn));
			}
		}
		timer = new Timer();
		timer.schedule(new SendNextPart(this), (long) 2*estimatedRTT);
	}
	
	@Override
	public void sendPart(byte sequenceNumber, int partNumber) {
		super.sendPart(sequenceNumber, partNumber);
		sendingTimes.put(sequenceNumber, now());
		System.out.format("Sending part %d\n", partNumber);
	}

	public void updateTransmittingWindow() {
		boolean acked = acks.get(seqNumToAck);
		while (acked) {
			byte largestFrameToSend = (byte) (seqNumToAck + windowSize);
			int largestPartToSend = sequenceNumberToFilePart.get(seqNumToAck) + windowSize;
			sequenceNumberToFilePart.put(largestFrameToSend, largestPartToSend);
			acks.put(largestFrameToSend, false);
			
			sequenceNumberToFilePart.remove(seqNumToAck);
			acks.remove(seqNumToAck);
			
			seqNumToAck++;
			acked = acks.get(seqNumToAck);
		}
	}
	
	public void updateReceivingWindow() {
		boolean acked = acks.get(seqNumToAck);
		while (acked) {
			byte largestFrameToSend = (byte) (seqNumToAck + windowSize);
			int largestPartToSend = sequenceNumberToFilePart.get(seqNumToAck) + windowSize;
			sequenceNumberToFilePart.put(largestFrameToSend, largestPartToSend);
			acks.put(largestFrameToSend, false);
			
			sequenceNumberToFilePart.remove(seqNumToAck);
			acks.remove(seqNumToAck);
			
			seqNumToAck++;
			acked = acks.get(seqNumToAck);
		}
	}
	
	public void doubleEstimatedRTT() {
		estimatedRTT = (long) 1.5*estimatedRTT;
		System.out.format("Doubled estimated RTT to %d\n", estimatedRTT);
	}
	
	public void updateEstimatedRTT(byte sequenceNumber, Date date) {
		long measuredRTT = date.getTime() - sendingTimes.get(sequenceNumber);
		estimatedRTT = (long) (0.6*measuredRTT + 0.4*estimatedRTT);
		System.out.format("Updated estimated RTT to %d\n", estimatedRTT);
	}
	
	private void initializeSlidingWindow() {
		sequenceNumberToFilePart = new ConcurrentHashMap<Byte, Integer>();
		acks = new ConcurrentHashMap<Byte, Boolean>();
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
	public ConcurrentHashMap<Byte, Integer> slidingWindow() {
		return this.sequenceNumberToFilePart;
	}
	
	public ConcurrentHashMap<Byte, Boolean> acks() {
		return this.acks;
	}
	
	private class SendNextPart extends TimerTask {
		private SlidingWindowStrategy strategy;
		
		public SendNextPart(SlidingWindowStrategy strategy) {
			this.strategy = strategy;
		}
		
		public void run() {
			strategy.sendAllFramesAgain();
			System.out.println("had a timeout");
			this.strategy.doubleEstimatedRTT();
			this.strategy.connection().timeOut();
		}
	}
}
