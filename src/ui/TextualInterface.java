package ui;

import java.util.ArrayList;

public class TextualInterface implements View {
	private InteractionController controller;
	
	public TextualInterface(InteractionController controller) {
		this.controller = controller;
	}
	
	public void showOptions() {
		show("The options are:");
		show(String.join("\n", controller.getOptions()));
	}
	
	public void showError(String errorMessage) {
		show(String.format("Someting went wrong: %s", errorMessage));
	}
	
	public void showStats(String filename, double fraction, int bytesPerSecond) {
		show(String.format("%s: %.1f %, %d bytes/s", filename, fraction*100, bytesPerSecond));
	}
	
	public void list(ArrayList<String> list) {
		String[] myArray = new String[0];
		show(String.join("\n", list.toArray(myArray)));
	}
	
	private void show(String message) {
		System.out.println(message);
	}
	
}
