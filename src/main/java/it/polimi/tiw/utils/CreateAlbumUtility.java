package it.polimi.tiw.utils;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Random;
import javax.servlet.http.HttpServletResponse;

public class CreateAlbumUtility {
	private HttpServletResponse response;
	
	public CreateAlbumUtility(HttpServletResponse response) {
		this.response = response;
	}
	
	public void invalidRequest(String errorMessage) throws IOException {
		response.sendRedirect("home?error=" + URLEncoder.encode(errorMessage, "UTF-8"));
	}
	
	public static String getRandomFilePath(String targetPath, String extension) {
		int targetStringLength = 10;
	    Random random = new Random();
	    boolean file_exists = true;
	    String generatedString = "";
	    while (file_exists) {
		    // Generate random alphanumeric string of 10 characters
		    generatedString = random.ints(48, 122 + 1)
		      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
		      .limit(targetStringLength)
		      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
		      .toString();
		    // Check if path exists
		    File f = new File(targetPath + generatedString + "." + extension);
		    if (!f.exists() && !f.isDirectory()) {
		    	file_exists = false;
		    }
	    }
	    return generatedString + extension;
	}
	
	// Check if a string is numeric
	public static boolean isNumeric(String s) {
		if (s==null) {
			return false;
		}
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
