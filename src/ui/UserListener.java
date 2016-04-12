package ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserListener {
	
	public UserListener() {
		
	}
	
	public static String readString() {
		String antw = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			antw = in.readLine();
		} catch (IOException e) {
		}

		return (antw == null) ? "" : antw;
	}
	
	public static int readInt(String input) throws IllegalArgumentException {
		int integer;
		String pattern = "([\\d]+)"; 
		Matcher matcher = Pattern.compile(pattern).matcher(input); 
            if (matcher.find()) {
                integer = Integer.valueOf(matcher.group(1));
            } else {
            	throw new IllegalArgumentException("There is no integer");
            }
        return integer;
    }

	public static char readChar(String input) throws IllegalArgumentException {
        char character;
        String pattern = "[A-Za-z]";
		Matcher matcher = Pattern.compile(pattern).matcher(input); 
        if (matcher.find()) {
            character = matcher.group(0).toUpperCase().charAt(0);
        } else {
        	throw new IllegalArgumentException("There is no character.");
        }
        return character;
    }
	

}
