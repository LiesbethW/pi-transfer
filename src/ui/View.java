package ui;

import java.util.ArrayList;

public interface View {

	public void showOptions();
	
	public void showError(String errorMessage);
	
	public void showStats(String filename, double fraction, int bytesPerSecond);
	
	public void list(ArrayList<String> list);
	
}
