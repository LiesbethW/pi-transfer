package berryPicker;

import java.util.Date;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class FileStats implements Observer {
	private static int MILLISPERSECOND = 1000;
	
	private FileObject file;
	private int totalPackets;
	private boolean[] packets;
	private HashMap<Long, Integer> bytesPerSecond;
	
	public FileStats(FileObject file) {
		this.file = file;
		bytesPerSecond = new HashMap<Long, Integer>();
		initializeStats();
	}
	
	public String display() {
		return String.format("%d%, %s", fraction()*100, speed());
	}
	
	@Override
	public void update(Observable o, Object arg) {
		int partNumber = ((Integer) arg).intValue();
		if (!packets[partNumber]) {
			packets[partNumber] = true;
			totalPackets++;
			long now = (new Date()).getTime();
			long second = now - (long) (now % MILLISPERSECOND);
			if (bytesPerSecond.containsKey(second)) {
				bytesPerSecond.put(second, bytesPerSecond.get(second) + 1);
			} else {
				bytesPerSecond.put(second, 1);
			}
		}
	}
	
	public boolean ready() {
		return totalPackets == file().numberOfParts();
	}
	
	public double fraction() {
		return totalPackets / file().numberOfParts();
	}
	
	public String speed() {
		long now = (new Date()).getTime();
		long lastSecond = now - MILLISPERSECOND - (long) (now % MILLISPERSECOND);
		if (bytesPerSecond.containsKey(lastSecond)) {
			int bps = bytesPerSecond.get(lastSecond)*this.file().getBytesPerPart();
			return String.format("%d B/S", bps);
		} else {
			return "0 B/S";
		}
	}
	
	public void initializeStats() {
		System.out.println("Initializing stats");
		totalPackets = 0;
		packets = new boolean[totalPackets];
		
	}
	
	private FileObject file() {
		return file;
	}

}
