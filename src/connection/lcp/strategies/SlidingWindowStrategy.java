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
		estimatedRTT = 1000;
		sendingTimes = new ConcurrentHashMap<Byte, Long>();
		initializeSlidingWindow();
	}
	
	@Override
	public void startTransmission() {
		sendNextPart();
		Timer timer = new Timer();
		timer.schedule(new SendNextPart(this), (long) 2*estimatedRTT);
	}

	@Override
	public void handleFilePart(LcpPacket packet) {
		byte sequenceNumber = packet.getSequenceNumber();
		System.out.format("Received packet %d\n", sequenceNumber);
		if (sequenceNumberToFilePart.containsKey((Byte) sequenceNumber)) {
			this.savePart(packet.getData(), sequenceNumberToFilePart.get((Byte) sequenceNumber).intValue());
			acks.put(sequenceNumber, true);
			updateReceivingWindow();
		}
		this.sendAck((byte) (seqNumToAck - 1));
	}

	@Override
	public void handleAck(LcpPacket packet) {
		timer.cancel();
		connection().resetTimeOuts();
		
		updateEstimatedRTT(packet.getSequenceNumber(), new Date());
		byte sequenceNumber = packet.getSequenceNumber();
		System.out.format("Received ack %d\n", sequenceNumber);
		for (byte b = (byte) (seqNumToAck - 1); (b^(sequenceNumber + 1)) != 0; b++) {
			if (acks.containsKey(b)) {
				acks.put(b, true);
				System.out.format("Set %d true\n", b);
			}
		}
		System.out.println("After updating acks: " + this.acks().entrySet());
		if (sequenceNumberToFilePart.containsKey(sequenceNumber) && 
				sequenceNumberToFilePart.get(sequenceNumber) >= connection().getFile().lastPart()) {
			this.connection().setTransmissionCompleted();
		}
		updateTransmittingWindow();
		
		sendNextPart();
		timer = new Timer();
		timer.schedule(new SendNextPart(this), (long) 2*estimatedRTT); 		
	}
	
	private void sendNextPart() {
		for (Byte sn : sequenceNumberToFilePart.keySet()) {
			if (!acks.get(sn)) {
				if (!sendingTimes.containsKey(sn)) {
					this.sendPart(sn, sequenceNumberToFilePart.get(sn));
					break;
				} else if (now() - sendingTimes.get(sn) > 2*estimatedRTT) {
					doubleEstimatedRTT();
					this.sendPart(sn, sequenceNumberToFilePart.get(sn));
					break;
				}
			}
		}
//		timer = new Timer();
//		timer.schedule(new SendNextPart(this), (long) estimatedRTT/windowSize); 
	}
	
	@Override
	public void sendPart(byte sequenceNumber, int partNumber) {
		super.sendPart(sequenceNumber, partNumber);
		sendingTimes.put(sequenceNumber, now());
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
		System.out.println("Sequence Number to file part: " + this.slidingWindow().entrySet());
		System.out.println("Acks: " + this.acks().entrySet());
		System.out.println("Now the next sequence number that I expect an ack from is " + seqNumToAck);
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
		System.out.println("Sequence Number to file part: " + this.slidingWindow().entrySet());
		System.out.println("Acks: " + this.acks().entrySet());
		System.out.println("Now the next sequence number that I expect a packet from is " + seqNumToAck);
	}
	
	public void doubleEstimatedRTT() {
		estimatedRTT = 2*estimatedRTT;
	}
	
	public void updateEstimatedRTT(byte sequenceNumber, Date date) {
		if (sendingTimes.contains(sequenceNumber)) {
			long measuredRTT = date.getTime() - sendingTimes.get(sequenceNumber);
			estimatedRTT = (long) (0.6*measuredRTT + 0.4*estimatedRTT);
			System.out.format("Updated estimated RTT to %d\n", estimatedRTT);
		}
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
	
	private byte nextSequenceNumber(byte sequenceNumber) {
		return (byte) ((sequenceNumber + 1) % 100);
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
			System.out.println("had a timeout");
			this.strategy.connection().timeOut();
			this.strategy = strategy;
		}
		
		public void run() {
			strategy.sendNextPart();
		}
	}
}
